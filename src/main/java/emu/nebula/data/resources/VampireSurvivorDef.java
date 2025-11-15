package emu.nebula.data.resources;

import emu.nebula.data.BaseDef;
import emu.nebula.data.ResourceType;
import lombok.Getter;

@Getter
@ResourceType(name = "VampireSurvivor.json")
public class VampireSurvivorDef extends BaseDef {
    private int Id;
    private int Mode;
    private int NeedWorldClass;
    
    @Override
    public int getId() {
        return Id;
    }
}
