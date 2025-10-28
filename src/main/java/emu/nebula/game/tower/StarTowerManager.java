package emu.nebula.game.tower;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import emu.nebula.Nebula;
import emu.nebula.data.GameData;
import emu.nebula.database.GameDatabaseObject;
import emu.nebula.game.player.Player;
import emu.nebula.game.player.PlayerManager;
import emu.nebula.proto.StarTowerApply.StarTowerApplyReq;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;

@Getter
@Entity(value = "star_tower", useDiscriminator = false)
public class StarTowerManager extends PlayerManager implements GameDatabaseObject {
    @Id
    private int uid;
    
    private transient Int2ObjectMap<StarTowerBuild> builds;
    private transient StarTowerInstance instance;
    private transient StarTowerBuild lastBuild;
    
    @Deprecated // Morphia only
    public StarTowerManager() {
        
    }
    
    public StarTowerManager(Player player) {
        super(player);
        this.uid = player.getUid();
        
        this.save();
    }
    
    public Int2ObjectMap<StarTowerBuild> getBuilds() {
        if (this.builds == null) {
            this.loadFromDatabase();
        }
        
        return builds;
    }
    
    public StarTowerInstance apply(StarTowerApplyReq req) {
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
        
        // Create instance
        this.instance = new StarTowerInstance(this, data, formation, req);
        
        // Success
        return this.instance;
    }

    public StarTowerInstance giveUp() {
        // Cache instance
        var instance = this.instance;
        
        if (instance != null) {
            // Set last build
            this.lastBuild = instance.getBuild();
            
            // Clear instance
            this.instance = null;
        }
        
        return instance;
    }
    
    public boolean saveBuild(boolean delete, String name, boolean lock) {
        // Sanity check
        if (this.getLastBuild() == null) {
            return false;
        }
        
        // Cache build and clear reference
        var build = this.lastBuild;
        this.lastBuild = null;
        
        // Check if the player wants this build or not
        if (delete) {
            return true;
        }
        
        // Check limit
        if (this.getBuilds().size() >= 50) {
            return false;
        }
        
        // Add to builds
        this.getBuilds().put(build.getUid(), build);
        
        // Save build to database
        build.save();
        
        //
        return true;
    }
    
    // Database
    
    private void loadFromDatabase() {
        this.builds = new Int2ObjectOpenHashMap<>();
        
        Nebula.getGameDatabase().getObjects(StarTowerBuild.class, "playerUid", getPlayerUid()).forEach(build -> {
            this.builds.put(build.getUid(), build);
        });
    }
}
