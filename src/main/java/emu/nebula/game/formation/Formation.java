package emu.nebula.game.formation;

import dev.morphia.annotations.Entity;
import emu.nebula.proto.Public.FormationInfo;
import lombok.Getter;

@Getter
@Entity(useDiscriminator = false)
public class Formation {
    private int num;
    private int[] charIds;
    private int[] discIds;
    
    @Deprecated
    public Formation() {
        
    }
    
    public Formation(int num) {
        this.num = num;
        this.charIds = new int[3];
        this.discIds = new int[6];
    }
    
    public Formation(FormationInfo formation) {
        this.num = formation.getNumber();
        this.charIds = formation.getCharIds().toArray();
        this.discIds = formation.getDiscIds().toArray();
    }

    public int getCharIdAt(int i) {
        if (i < 0 || i >= this.charIds.length) {
            return -1;
        }
        
        return this.charIds[i];
    }

    public int getDiscIdAt(int i) {
        if (i < 0 || i >= this.discIds.length) {
            return -1;
        }
        
        return this.discIds[i];
    }
    
    public int getCharCount() {
        int count = 0;
        
        for (int id : this.getCharIds()) {
            if (id > 0) {
                count++;
            }
        }
        
        return count;
    }

    public int getDiscCount() {
        int count = 0;
        
        for (int id : this.getDiscIds()) {
            if (id > 0) {
                count++;
            }
        }
        
        return count;
    }
    
    // Proto
    
    public FormationInfo toProto() {
        var proto = FormationInfo.newInstance()
                .setNumber(this.getNum())
                .addAllCharIds(this.getCharIds())
                .addAllDiscIds(this.getDiscIds());
        
        return proto;
    }
}
