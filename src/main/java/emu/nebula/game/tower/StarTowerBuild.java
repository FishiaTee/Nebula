package emu.nebula.game.tower;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;
import emu.nebula.data.GameData;
import emu.nebula.database.GameDatabaseObject;
import emu.nebula.proto.PublicStarTower.BuildPotential;
import emu.nebula.proto.PublicStarTower.StarTowerBuildBrief;
import emu.nebula.proto.PublicStarTower.StarTowerBuildDetail;
import emu.nebula.proto.PublicStarTower.StarTowerBuildInfo;
import emu.nebula.proto.PublicStarTower.TowerBuildChar;
import emu.nebula.util.Snowflake;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import lombok.Getter;

@Getter
@Entity(value = "star_tower_builds", useDiscriminator = false)
public class StarTowerBuild implements GameDatabaseObject {
    @Id
    private int uid;
    @Indexed
    private int playerUid;
    
    private String name;
    private boolean lock;
    private boolean preference;
    private int score;
    
    private Int2IntMap chars;
    private int[] discIds;
    
    private Int2IntMap potentials;
    private Int2IntMap subNoteSkills;
    
    @Deprecated
    public StarTowerBuild() {
        
    }
    
    public StarTowerBuild(StarTowerInstance instance) {
        this.uid = Snowflake.newUid();
        this.playerUid = instance.getPlayer().getUid();
        this.name = "";
        this.potentials = new Int2IntOpenHashMap();
        this.subNoteSkills = new Int2IntOpenHashMap();
        
        // Discs
        this.discIds = instance.getDiscs().stream()
                .filter(d -> d.getId() > 0)
                .mapToInt(d -> d.getId())
                .toArray();
        
        // Characters
        this.chars = new Int2IntOpenHashMap();
        
        instance.getChars().stream()
                .forEach(c -> this.getChars().put(c.getId(), 0));
        
        // Add potentials
        for (int id : instance.getPotentials()) {
            // Add to potential map
            this.getPotentials().put(id, instance.getItemCount(id));
            
            // Add to character
            var potentialData = GameData.getPotentialDataTable().get(id);
            if (potentialData != null) {
                int charId = potentialData.getCharId();
                this.getChars().put(charId, this.getChars().get(charId) + 1);
            }
        }
    }
    
    // Proto
    
    public StarTowerBuildInfo toProto() {
        var proto = StarTowerBuildInfo.newInstance()
                .setBrief(this.toBriefProto())
                .setDetail(this.toDetailProto());

        return proto;
    }

    public StarTowerBuildBrief toBriefProto() {
        var proto = StarTowerBuildBrief.newInstance()
                .setId(this.getUid())
                .setName(this.getName())
                .setLock(this.isLock())
                .setPreference(this.isPreference())
                .setScore(this.getScore())
                .addAllDiscIds(this.getDiscIds());
        
        // Add characters
        for (var character : this.getChars().int2IntEntrySet()) {
            var charProto = TowerBuildChar.newInstance()
                    .setCharId(character.getIntKey())
                    .setPotentialCnt(character.getIntValue());
            
            proto.addChars(charProto);
        }
        
        return proto;
    }
    
    public StarTowerBuildDetail toDetailProto() {
        var proto = StarTowerBuildDetail.newInstance();
        
        for (var entry : this.getPotentials().int2IntEntrySet()) {
            var potential = BuildPotential.newInstance()
                    .setPotentialId(entry.getIntKey())
                    .setLevel(entry.getIntValue());
            
            proto.getMutablePotentials().add(potential);
        }
        
        return proto;
    }
}
