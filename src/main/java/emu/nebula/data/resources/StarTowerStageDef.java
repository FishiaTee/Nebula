package emu.nebula.data.resources;

import emu.nebula.data.BaseDef;
import emu.nebula.data.ResourceType;
import lombok.Getter;

@Getter
@ResourceType(name = "StarTowerStage.json")
public class StarTowerStageDef extends BaseDef {
    private int Id;
    private int Stage;
    private int Floor;
    private int RoomType;
    private int InteriorCurrencyQuantity;
    
    @Override
    public int getId() {
        return Id;
    }
}
