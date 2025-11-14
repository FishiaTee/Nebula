package emu.nebula.game.friends;

import java.util.ArrayList;
import java.util.List;

import emu.nebula.GameConstants;
import emu.nebula.Nebula;
import emu.nebula.game.GameContext;
import emu.nebula.game.player.Player;
import emu.nebula.game.player.PlayerManager;
import emu.nebula.net.NetMsgId;
import emu.nebula.proto.FriendListGet.FriendListGetResp;
import emu.nebula.proto.Public.FriendDetail;
import emu.nebula.proto.Public.FriendState;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;

@Getter
public class FriendList extends PlayerManager {
    private final Int2ObjectMap<Friendship> friends;
    private final Int2ObjectMap<Friendship> pendingFriends;
    
    private long cacheCooldown;
    private FriendListGetResp cachedProto;
    
    public FriendList(Player player) {
        super(player);
        this.friends = new Int2ObjectOpenHashMap<Friendship>();
        this.pendingFriends = new Int2ObjectOpenHashMap<Friendship>();
    }
    
    private GameContext getGameContext() {
        return Nebula.getGameContext();
    }
    
    public boolean isLoaded() {
        return this.getPlayer().isLoaded();
    }
    
    private synchronized Friendship getFriendById(int id) {
        if (this.isLoaded()) {
            return this.getFriends().get(id);
        } else {
            return Nebula.getGameDatabase().getObjectByUid(Friendship.class, Friendship.generateUniqueKey(getPlayerUid(), id));
        }
    }

    private synchronized Friendship getPendingFriendById(int id) {
        if (this.isLoaded()) {
            return this.getPendingFriends().get(id);
        } else {
            return Nebula.getGameDatabase().getObjectByUid(Friendship.class, Friendship.generateUniqueKey(getPlayerUid(), id));
        }
    }
    
    public synchronized boolean hasPendingRequests() {
        return this.getPendingFriends().values()
                .stream()
                .filter(f -> f.getAskerUid() != this.getPlayerUid())
                .findAny()
                .isPresent();
    }
    
    private void addFriendship(Friendship friendship) {
        getFriends().put(friendship.getFriendUid(), friendship);
        this.cacheCooldown = 0;
    }

    private void addPendingFriendship(Friendship friendship) {
        getPendingFriends().put(friendship.getFriendUid(), friendship);
        this.cacheCooldown = 0;
    }
    
    private void removeFriendship(int uid) {
        getFriends().remove(uid);
        this.cacheCooldown = 0;
    }

    private void removePendingFriendship(int uid) {
        getPendingFriends().remove(uid);
        this.cacheCooldown = 0;
    }
    
    /**
     * Gets total amount of potential friends
     */
    public int getFullFriendCount() {
        return this.getPendingFriends().size() + this.getFriends().size();
    }
    
    public synchronized Player handleFriendRequest(int targetUid, boolean action) {
        // Make sure we have enough room
        if (this.getFriends().size() >= GameConstants.MAX_FRIENDSHIPS) {
            return null;
        }
        
        // Check if player has sent friend request
        Friendship myFriendship = this.getPendingFriendById(targetUid);
        if (myFriendship == null) {
            return null;
        }
        
        // Make sure this player is not the asker
        if (myFriendship.getAskerUid() == this.getPlayer().getUid()) {
            return null;
        }
        
        // Get target player
        Player target = getGameContext().getPlayerModule().getPlayer(targetUid);
        if (target == null) return null;
        
        // Get target player's friendship
        Friendship theirFriendship = target.getFriendList().getPendingFriendById(getPlayer().getUid());
        
        if (theirFriendship == null) {
            // They dont have us on their friends list anymore, rip
            this.removePendingFriendship(target.getUid());
            myFriendship.delete();
            return null;
        }
        
        // Handle action
        if (action) {
            // Request accepted
            myFriendship.setFriend(true);
            theirFriendship.setFriend(true);

            this.removePendingFriendship(myFriendship.getFriendUid());
            this.addFriendship(myFriendship);

            if (target.isLoaded()) {
                target.getFriendList().removePendingFriendship(this.getPlayer().getUid());
                target.getFriendList().addFriendship(theirFriendship);
            }

            // Save friendships to the database
            myFriendship.save();
            theirFriendship.save();
        } else {
            // Request declined - Delete from my pending friends
            this.removePendingFriendship(myFriendship.getPlayerUid());

            if (target.isLoaded()) {
                target.getFriendList().removePendingFriendship(getPlayer().getUid());
            }
            
            // Delete friendships from the database
            myFriendship.delete();
            theirFriendship.delete();
        }
        
        // Success
        return target;
    }

    public List<Player> acceptAll() {
        // Results
        List<Player> results = new ArrayList<>();
        
        // Get list of friendships to accept
        List<Friendship> list = getPendingFriends().values()
                .stream()
                .toList();
        
        for (var invite : list) {
            var target = this.handleFriendRequest(invite.getFriendUid(), true);
            
            if (target != null) {
                results.add(target); 
            }
        }
        
        return results;
    }

    public synchronized boolean sendFriendRequest(int targetUid) {
        // Get target and sanity check
        Player target = getGameContext().getPlayerModule().getPlayer(targetUid);
        if (target == null || target == this.getPlayer()) {
            return false;
        }
        
        // Check if friend already exists
        if (getPendingFriends().containsKey(targetUid) || getFriends().containsKey(targetUid)) {
            return false;
        }
        
        // Create friendships
        Friendship myFriendship = new Friendship(getPlayer(), target, getPlayer());
        Friendship theirFriendship = new Friendship(target, getPlayer(), getPlayer());
        
        // Add to our pending friendship list
        this.addPendingFriendship(myFriendship);

        if (target.isLoaded()) {
            target.getFriendList().addPendingFriendship(theirFriendship);
            
            // Send message to notify target
            target.addNextPackage(
                NetMsgId.friend_state_notify, 
                FriendState.newInstance()
                    .setId(this.getPlayerUid())
                    .setAction(1)
            );
        }
        
        // Save friendships to the database
        myFriendship.save();
        theirFriendship.save();
        
        // Success
        return true;
    }
    
    public synchronized boolean deleteFriend(int targetUid) {
        // Get friendship
        Friendship myFriendship = this.getFriendById(targetUid);
        if (myFriendship == null) return false;

        // Remove from friends list
        this.removeFriendship(targetUid);
        myFriendship.delete();

        // Delete from friend's friend list
        Player friend = getGameContext().getPlayerModule().getPlayer(targetUid);
        
        if (friend != null) {
            // Friend online
            Friendship theirFriendship = friend.getFriendList().getFriendById(this.getPlayer().getUid());
            
            if (theirFriendship != null) {
                // Delete friendship on friends side
                theirFriendship.delete();
                
                if (friend.isLoaded()) {
                    // Remove from online friend's friend list
                    friend.getFriendList().removeFriendship(theirFriendship.getFriendUid());
                }
            }
        }
        
        // Success
        return true;
    }
    
    // Database
    
    public synchronized void loadFromDatabase() {
        var friendships = Nebula.getGameDatabase().getObjects(Friendship.class, "ownerUid", this.getPlayer().getUid());
        
        friendships.forEach(friendship -> {
            // Set ownership first
            friendship.setPlayer(this.getPlayer());
            
            // Finally, load to our friends list
            if (friendship.isFriend()) {
                getFriends().put(friendship.getFriendUid(), friendship);
            } else {
                getPendingFriends().put(friendship.getFriendUid(), friendship);
            }
        });
    }
    
    // Proto 
    
    public synchronized FriendListGetResp toProto() {
        if (this.cachedProto == null || System.currentTimeMillis() > this.cacheCooldown) {
            this.cachedProto = this.updateCache();
            this.cacheCooldown = System.currentTimeMillis() + 60_000;
        }
        
        return this.cachedProto;
    }
    
    private FriendListGetResp updateCache() {
        var proto = FriendListGetResp.newInstance();
        
        // Encode friends list
        for (var friend : getFriends().values()) {
            // Get base friend info
            var base = friend.toProto();
            if (base == null) continue;
            
            // Create info
            var info = FriendDetail.newInstance()
                    .setBase(base)
                    .setGetEnergy(friend.getEnergy());
            
            // Add
            proto.addFriends(info);
        }
        
        // Encode pending invites
        for (var friend : getPendingFriends().values()) {
            // Skip if this is us
            if (friend.getAskerUid() == this.getPlayerUid()) {
                continue;
            }
            
            // Get base friend info
            var base = friend.toProto();
            if (base == null) continue;
            
            // Add
            proto.addInvites(base);
        }
        
        return proto;
    }
}
