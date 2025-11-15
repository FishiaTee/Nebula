package emu.nebula.game.vampire;

import dev.morphia.annotations.Entity;
import emu.nebula.proto.Public.VampireSurvivorLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity(useDiscriminator = false)
public class VampireSurvivorLog {
    private int id;
    
    @Setter private int score;
    @Setter private boolean passed;
    @Setter private long[] builds;
    
    @Deprecated // Morphia only
    public VampireSurvivorLog() {
        
    }
    
    public VampireSurvivorLog(int id) {
        this.id = id;
    }
    
    // Proto
    
    public VampireSurvivorLevel toProto() {
        var proto = VampireSurvivorLevel.newInstance()
                .setId(this.getId())
                .setScore(this.getScore())
                .setPassed(this.isPassed());
        
        if (this.builds != null) {
            for (long buildId : this.builds) {
                proto.addBuildIds(buildId);
            }
        }
        
        return proto;
    }
}
