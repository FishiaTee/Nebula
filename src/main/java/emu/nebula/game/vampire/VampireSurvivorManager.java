package emu.nebula.game.vampire;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import emu.nebula.data.GameData;
import emu.nebula.database.GameDatabaseObject;
import emu.nebula.game.player.Player;
import emu.nebula.game.player.PlayerManager;

import lombok.Getter;

@Getter
@Entity(value = "vampire", useDiscriminator = false)
public class VampireSurvivorManager extends PlayerManager implements GameDatabaseObject {
    @Id
    private int uid;
    
    // Game
    private transient VampireSurvivorGame game;
    
    // TODO talents
    
    @Deprecated // Morphia only
    public VampireSurvivorManager() {
        
    }
    
    public VampireSurvivorManager(Player player) {
        super(player);
        this.uid = player.getUid();
        
        //this.save();
    }
    
    public VampireSurvivorGame apply(int levelId) {
        // Get data
        var data = GameData.getVampireSurvivorDataTable().get(levelId);
        if (data == null) {
            return null;
        }
        
        // Create game
        this.game = new VampireSurvivorGame(this, data);
        
        // Success
        return this.game;
    }

    public void settle(boolean isWin, int score) {
        // Clear game
        this.game = null;
    }

}
