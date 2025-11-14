package emu.nebula.game.friends;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;
import emu.nebula.Nebula;
import emu.nebula.database.GameDatabaseObject;
import emu.nebula.game.player.Player;
import emu.nebula.proto.Public.Friend;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity(value = "friendships", useDiscriminator = false)
public class Friendship implements GameDatabaseObject {
    @Id private long key;
    
    @Indexed private int playerUid;
    @Indexed private int friendUid;
    private int askerUid;
    
    @Setter private boolean isFriend;
    @Setter private boolean star;
    @Setter private int energy;
    
    @Setter private transient Player player;
    
    @Deprecated // Morphia use only
    public Friendship() { }
    
    public Friendship(Player player, Player friend, Player asker) {
        this.player = player;
        this.playerUid = player.getUid();
        this.friendUid = friend.getUid();
        this.askerUid = asker.getUid();
        this.key = Friendship.generateUniqueKey(player.getUid(), friend.getUid());
    }
    
    // Database functions

    public void delete() {
        Nebula.getGameDatabase().delete(this);
    }
    
    // Proto
    
    public Friend toProto() {
        // Get target player
        var target = Nebula.getGameContext().getPlayerModule().getPlayer(this.getFriendUid());
        if (target == null) return null;
        
        // Encode player to simple friend proto
        return target.getFriendProto();
    }
    
    // Extra
    
    /**
     * Creates an unique key for a friendship object using 2 player uids
     */
    public static long generateUniqueKey(int ownerUid, int targetUid) {
        return ((long) ownerUid << 32) + targetUid;
    }
}
