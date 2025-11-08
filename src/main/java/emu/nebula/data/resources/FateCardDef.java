package emu.nebula.data.resources;

import emu.nebula.data.BaseDef;
import emu.nebula.data.ResourceType;
import lombok.Getter;

@Getter
@ResourceType(name = "FateCard.json")
public class FateCardDef extends BaseDef {
    private int Id;
    
    private boolean IsTower;
    private boolean IsVampire;
    private boolean IsVampireSpecial;
    private boolean Removable;
    
    @Override
    public int getId() {
        return Id;
    }
}
