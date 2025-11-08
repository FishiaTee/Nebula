package emu.nebula.game.vampire;

import emu.nebula.data.GameData;
import emu.nebula.data.resources.VampireSurvivorDef;
import emu.nebula.proto.Public.CardInfo;
import emu.nebula.proto.Public.VampireSurvivorLevelReward;
import emu.nebula.util.WeightedList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.Getter;

@Getter
public class VampireSurvivorGame {
    private final VampireSurvivorManager manager;
    private final VampireSurvivorDef data;
    
    private IntSet cards;
    
    private int rewardLevel;
    private IntList rewards;
    
    public VampireSurvivorGame(VampireSurvivorManager manager, VampireSurvivorDef data) {
        this.manager = manager;
        this.data = data;
        
        this.cards = new IntOpenHashSet();
        this.rewards = new IntArrayList();
        
        this.calcRewards();
    }
    
    public void calcRewards() {
        // Clear reward list first
        this.rewards.clear();
        
        // Increment level
        this.rewardLevel++;
        
        var random = new WeightedList<Integer>();
        for (var card : GameData.getFateCardDataTable()) {
            // Filter only vampire surv cards
            if (!card.isIsVampire()) {
                continue;
            }
            
            // Skip cards we already have
            if (this.getCards().contains(card.getId())) {
                continue;
            }
            
            // Add
            random.add(100, card.getId());
        }
        
        // Add 2 rewards
        this.getRewards().add(random.next().intValue());
        this.getRewards().add(random.next().intValue());
    }
    
    public int selectReward(int index, boolean reRoll) {
        // Sanity check
        if (index < 0 || index >= this.getRewards().size()) {
            return -1;
        }
        
        // Get fate card id
        int id = this.getRewards().getInt(index);
        
        // Add to cards
        this.getCards().add(id);
        
        // Reroll rewards
        this.calcRewards();
        
        // Success
        return id;
    }

    // Proto
    
    public VampireSurvivorLevelReward getRewardProto() {
        var proto = VampireSurvivorLevelReward.newInstance()
                .setLevel(this.getRewardLevel());
        
        var pkg = proto.getMutablePkg();
        
        for (int id : this.getRewards()) {
            var card = CardInfo.newInstance()
                    .setId(id);
            
            pkg.addCards(card);
        }
        
        return proto;
    }

}
