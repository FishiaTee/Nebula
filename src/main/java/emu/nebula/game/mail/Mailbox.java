package emu.nebula.game.mail;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;

import emu.nebula.Nebula;
import emu.nebula.database.GameDatabaseObject;
import emu.nebula.game.player.Player;
import emu.nebula.game.player.PlayerChangeInfo;
import emu.nebula.game.player.PlayerManager;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.Getter;

@Getter
@Entity(value = "mailbox", useDiscriminator = false)
public class Mailbox extends PlayerManager implements GameDatabaseObject, Iterable<GameMail> {
    @Id
    private int uid;
    private int lastMailId;
    
    private List<GameMail> list;
    
    @Deprecated // Morphia only
    public Mailbox() {
        
    }
    
    public Mailbox(Player player) {
        super(player);
        
        this.uid = player.getUid();
        this.list = new ArrayList<>();
        
        this.save();
    }
    
    // TODO optimize to an O(n) algorithm like a map
    public GameMail getMailById(int id) {
        return this.getList().stream()
                .filter(m -> m.getId() == id)
                .findFirst()
                .orElse(null);
    }
    
    public void sendMail(GameMail mail) {
        // Set mail id
        mail.setId(++this.lastMailId);
        
        // Add to mail list
        this.list.add(mail);
        
        // Save to database
        Nebula.getGameDatabase().update(this, getUid(), "lastMailId", this.getLastMailId());
        Nebula.getGameDatabase().addToList(this, getUid(), "list", mail);
    }
    
    public boolean readMail(int id, long flag) {
        // Get mail
        var mail = this.getMailById(id);
        
        if (mail == null) {
            return false;
        }
        
        // Set read
        mail.setRead(true);
        
        // Update in database
        Nebula.getGameDatabase().updateNested(this, getUid(), "list.id", id, "list.$.read", true);
        
        // Success
        return true;
    }
    
    public GameMail pinMail(int id, long flag, boolean pin) {
        // Get mail
        var mail = this.getMailById(id);
        
        if (mail == null) {
            return null;
        }
        
        // Set pin
        mail.setPin(pin);
        
        // Update in database
        Nebula.getGameDatabase().updateNested(this, getUid(), "list.id", id, "list.$.pin", true);
        
        // Success
        return mail;
    }
    
    public PlayerChangeInfo recvMail(Player player, int id) {
        // Get mails that we want to claim
        List<GameMail> mails = null;
        
        if (id == 0) {
            // Claim all
            mails = this.getList()
                    .stream()
                    .filter(mail -> !mail.isRecv() && mail.hasAttachments())
                    .toList();
        } else {
            // Claim one
            var mail = this.getMailById(id);
            
            if (mail != null && !mail.isRecv() && mail.hasAttachments()) {
                mails = List.of(mail);
            }
        }
        
        // Create change info
        var changes = new PlayerChangeInfo();
        
        // Sanity
        if (mails == null || mails.isEmpty()) {
            return changes;
        }
        
        // Recieved mail id list
        var recvMails = new IntArrayList();
        
        // Recv mails
        for (var mail : mails) {
            // Add attachments to player
            player.getInventory().addItems(mail.getAttachments(), changes);
            
            // Set claimed flag
            mail.setRecv(true);
            
            // Add to recvied mail list
            recvMails.add(mail.getId());
            
            // Update in database
            Nebula.getGameDatabase().updateNested(this, getUid(), "list.id", mail.getId(), "list.$.recv", true);
        }
        
        // Set extra change data
        changes.setExtraData(recvMails);
        
        // Success
        return changes.setSuccess(true);
    }
    
    public IntList removeMail(Player player, int id) {
        // Get mails that we want to claim
        Set<GameMail> toRemove = null;
        
        if (id == 0) {
            // Claim all
            toRemove = this.getList()
                    .stream()
                    .filter(mail -> mail.canRemove())
                    .collect(Collectors.toSet());
        } else {
            // Claim one
            var mail = this.getMailById(id);
            
            if (mail != null && mail.canRemove()) {
                toRemove = Set.of(mail);
            }
        }
        
        // Recieved mail id list
        var removed = new IntArrayList();
        
        // Sanity check
        if (toRemove == null || toRemove.isEmpty()) {
            return removed;
        }
        
        // Remove
        var it = this.getList().iterator();
        while (it.hasNext()) {
            var mail = it.next();
            
            if (toRemove.contains(mail)) {
                removed.add(mail.getId());
                it.remove();
            }
        }
        
        // Save
        this.save();
        
        // Success
        return removed;
    }

    @Override
    public Iterator<GameMail> iterator() {
        return this.getList().iterator();
    }
}
