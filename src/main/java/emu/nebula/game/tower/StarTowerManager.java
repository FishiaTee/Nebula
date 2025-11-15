package emu.nebula.game.tower;

import emu.nebula.Nebula;
import emu.nebula.data.GameData;
import emu.nebula.game.player.Player;
import emu.nebula.game.player.PlayerChangeInfo;
import emu.nebula.game.player.PlayerManager;
import emu.nebula.game.quest.QuestCondType;
import emu.nebula.proto.StarTowerApply.StarTowerApplyReq;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import lombok.Getter;

@Getter
public class StarTowerManager extends PlayerManager {
    // Tower game instance
    private StarTowerGame game;
    
    // Tower builds
    private Long2ObjectMap<StarTowerBuild> builds;
    private StarTowerBuild lastBuild;
    
    public StarTowerManager(Player player) {
        super(player);
    }
    
    public Long2ObjectMap<StarTowerBuild> getBuilds() {
        if (this.builds == null) {
            this.loadFromDatabase();
        }
        
        return builds;
    }
    
    public StarTowerBuild getBuildById(long id) {
        return this.getBuilds().get(id);
    }

    public boolean hasBuild(long id) {
        return this.getBuilds().containsKey(id);
    }
    
    public StarTowerGame apply(StarTowerApplyReq req) {
        // Sanity checks
        var data = GameData.getStarTowerDataTable().get(req.getId());
        if (data == null) {
            return null;
        }
        
        // Get formation
        var formation = getPlayer().getFormations().getFormationById(req.getFormationId());
        if (formation == null) {
            return null;
        }
        
        // Make sure player has at least 3 chars and 3 discs
        if (formation.getCharCount() != 3 || formation.getDiscCount() < 3) {
            return null;
        }
        
        // Create game
        this.game = new StarTowerGame(this, data, formation, req);
        
        // Trigger quest
        this.getPlayer().triggerQuest(QuestCondType.TowerEnterFloor, 1);
        
        // Success
        return this.game;
    }

    public StarTowerGame endGame() {
        // Cache instance
        var game = this.game;
        
        if (game != null) {
            // Set last build
            this.lastBuild = game.getBuild();
            
            // Clear instance
            this.game = null;
        }
        
        return game;
    }
    
    // Build
    
    private PlayerChangeInfo dismantleBuild(StarTowerBuild build, PlayerChangeInfo change) {
        // Calculate quanity of tickets from record score
        int count = (int) Math.floor(build.getScore() / 100);
        
        // Add journey tickets
        this.getPlayer().getInventory().addItem(12, count, change);
        
        // Success
        return change;
    }
    
    public PlayerChangeInfo saveBuild(boolean delete, String name, boolean lock) {
        // Sanity check
        if (this.getLastBuild() == null) {
            return null;
        }
        
        // Create player change info
        var change = new PlayerChangeInfo();
        
        // Cache build and clear reference
        var build = this.lastBuild;
        this.lastBuild = null;
        
        // Check if the player wants this build or not
        if (delete) {
            return this.dismantleBuild(build, change);
        }
        
        // Check limit
        if (this.getBuilds().size() >= 50) {
            return null;
        }
        
        // Add to builds
        this.getBuilds().put(build.getUid(), build);
        
        // Save build to database
        build.save();
        
        // Success
        return change;
    }
    
    public PlayerChangeInfo deleteBuild(long buildId, PlayerChangeInfo change) {
        // Create change info
        if (change == null) {
            change = new PlayerChangeInfo();
        }
        
        // Get build
        var build = this.getBuilds().remove(buildId);
        
        if (build == null) {
            return change;
        }
        
        // Delete
        build.delete();
        
        // Add journey tickets
        this.dismantleBuild(build, change);
        
        // Success
        return change;
    }
    
    // Database
    
    public void loadFromDatabase() {
        this.builds = new Long2ObjectOpenHashMap<>();
        
        Nebula.getGameDatabase().getObjects(StarTowerBuild.class, "playerUid", getPlayerUid()).forEach(build -> {
            this.builds.put(build.getUid(), build);
        });
    }
}
