package emu.nebula.data.resources;

import emu.nebula.data.BaseDef;
import emu.nebula.data.ResourceType;
import emu.nebula.game.inventory.ItemParamMap;
import lombok.Getter;

@Getter
@ResourceType(name = "BattlePassReward.json")
public class BattlePassRewardDef extends BaseDef {
    private int ID;
    private int Level;
    
    private int Tid1;
    private int Qty1;
    private int Tid2;
    private int Qty2;
    private int Tid3;
    private int Qty3;
    
    private transient ItemParamMap basicRewards;
    private transient ItemParamMap premiumRewards;
    
    @Override
    public int getId() {
        return (ID << 16) + Level;
    }
    
    @Override
    public void onLoad() {
        this.basicRewards = new ItemParamMap();
        this.premiumRewards = new ItemParamMap();
        
        if (this.Tid1 > 0) {
            this.basicRewards.add(this.Tid1, this.Qty1);
        }
        
        if (this.Tid2 > 0) {
            this.premiumRewards.add(this.Tid2, this.Qty2);
        }
        if (this.Tid3 > 0) {
            this.premiumRewards.add(this.Tid3, this.Qty3);
        }
    }
}
