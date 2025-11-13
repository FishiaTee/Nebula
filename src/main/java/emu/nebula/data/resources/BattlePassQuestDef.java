package emu.nebula.data.resources;

import emu.nebula.data.BaseDef;
import emu.nebula.data.ResourceType;
import emu.nebula.game.quest.QuestData;
import emu.nebula.game.quest.QuestHelper;
import emu.nebula.game.quest.QuestType;
import emu.nebula.game.quest.QuestHelper.QuestParams;
import lombok.Getter;

@Getter
@ResourceType(name = "BattlePassQuest.json")
public class BattlePassQuestDef extends BaseDef implements QuestData {
    private int Id;
    private int Type;
    private int Exp;
    
    private transient int questType;
    private transient QuestParams params;
    
    @Override
    public int getId() {
        return Id;
    }
    
    public boolean isDaily() {
        return this.Type == 1;
    }
    
    @Override
    public int getCompleteCond() {
        return params.getCompleteCond();
    }

    @Override
    public int[] getCompleteCondParams() {
        return params.getCompleteCondParams();
    }
    
    @Override
    public void onLoad() {
        this.questType = this.isDaily() ? QuestType.BattlePassDaily : QuestType.BattlePassWeekly;
        this.params = QuestHelper.getBattlePassQuestParams().getOrDefault(this.getId(), QuestHelper.DEFAULT);
    }
}
