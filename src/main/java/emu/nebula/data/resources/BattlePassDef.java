package emu.nebula.data.resources;

import emu.nebula.data.BaseDef;
import emu.nebula.data.ResourceType;
import lombok.Getter;

@Getter
@ResourceType(name = "BattlePass.json")
public class BattlePassDef extends BaseDef {
    private int ID;
    
    @Override
    public int getId() {
        return ID;
    }
}
