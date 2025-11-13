package emu.nebula.data.resources;

import emu.nebula.data.BaseDef;
import emu.nebula.data.ResourceType;
import lombok.Getter;

@Getter
@ResourceType(name = "BattlePassLevel.json")
public class BattlePassLevelDef extends BaseDef {
    private int ID;
    private int Exp;

    private int Tid;
    private int Qty;
    
    @Override
    public int getId() {
        return ID;
    }
}
