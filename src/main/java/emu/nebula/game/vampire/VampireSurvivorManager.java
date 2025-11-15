package emu.nebula.game.vampire;

import emu.nebula.Nebula;
import emu.nebula.data.GameData;
import emu.nebula.game.player.Player;
import emu.nebula.game.player.PlayerManager;
import emu.nebula.game.player.PlayerProgress;
import emu.nebula.util.Bitset;
import lombok.Getter;
import us.hebi.quickbuf.RepeatedLong;

@Getter
public class VampireSurvivorManager extends PlayerManager {
    // Game
    private VampireSurvivorGame game;
    
    public VampireSurvivorManager(Player player) {
        super(player);
    }
    
    public PlayerProgress getProgress() {
        return this.getPlayer().getProgress();
    }

    public Bitset getTalents() {
        return this.getProgress().getVampireTalents();
    }
    
    public int getTalentPoints() {
        return this.getProgress().getVampireCards().size() * 5;
    }
    
    public VampireSurvivorGame apply(int levelId, RepeatedLong builds) {
        // Get data
        var data = GameData.getVampireSurvivorDataTable().get(levelId);
        if (data == null) {
            return null;
        }
        
        // Check player level (world class)
        if (this.getPlayer().getLevel() < data.getNeedWorldClass()) {
            return null;
        }
        
        // Check builds
        if (builds.length() != data.getMode()) {
            return null;
        }
        
        // Make sure our builds exist
        for (long buildId : builds) {
            boolean hasBuild = this.getPlayer().getStarTowerManager().hasBuild(buildId);
            if (hasBuild == false) {
                return null;
            }
        }
        
        // Create game
        this.game = new VampireSurvivorGame(this, data, builds.toArray());
        
        // Success
        return this.game;
    }

    public void settle(boolean isWin, int score) {
        // Sanity check
        if (this.game == null) {
            return;
        }
        
        // Skip if we didn't win
        if (!isWin) {
            return;
        }
        
        // Get log from database
        var log = this.getProgress().getVampireLog().computeIfAbsent(
            game.getId(),
            id -> new VampireSurvivorLog(id)
        );
        
        // Check if we should update score
        if (score >= log.getScore() || !log.isPassed()) {
            // Set record info
            log.setPassed(isWin);
            log.setScore(score);
            log.setBuilds(game.getBuilds());
            
            // Save record to database if we set any data
            Nebula.getGameDatabase().update(
                this.getProgress(),
                this.getPlayerUid(),
                "vampireLog." + game.getId(),
                log
            );
        }
        
        // Clear game
        this.game = null;
    }

}
