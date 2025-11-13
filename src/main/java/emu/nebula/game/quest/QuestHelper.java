package emu.nebula.game.quest;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;

// Because some quests in the data files do not have condition params, we will hardcode them here
public class QuestHelper {
    public static final QuestParams DEFAULT = new QuestParams(0, new int[1]);
    
    @Getter
    private static final Int2ObjectMap<QuestParams> battlePassQuestParams = new Int2ObjectOpenHashMap<>();
    
    static {
        battlePassQuestParams.put(1001, new QuestParams(QuestCondType.LoginTotal, 1));
        battlePassQuestParams.put(1002, new QuestParams(QuestCondType.EnergyDeplete, 160));
        battlePassQuestParams.put(1003, new QuestParams(QuestCondType.BattleTotal, 6));
        battlePassQuestParams.put(1004, new QuestParams(QuestCondType.QuestWithSpecificType, 5, QuestType.Daily));
        battlePassQuestParams.put(2001, new QuestParams(QuestCondType.TowerEnterFloor, 1));
        battlePassQuestParams.put(2002, new QuestParams(QuestCondType.WeekBoosClearSpecificDifficultyAndTotal, 3));
        battlePassQuestParams.put(2003, new QuestParams(QuestCondType.BattleTotal, 20));
        battlePassQuestParams.put(2004, new QuestParams(QuestCondType.LoginTotal, 5));
        battlePassQuestParams.put(2005, new QuestParams(QuestCondType.AgentFinishTotal, 3));
        battlePassQuestParams.put(2006, new QuestParams(QuestCondType.ItemsDeplete, 100000, 1));
        battlePassQuestParams.put(2007, new QuestParams(QuestCondType.GiftGiveTotal, 5));
        battlePassQuestParams.put(2008, new QuestParams(QuestCondType.EnergyDeplete, 1200));
    }
    
    @Getter
    public static class QuestParams {
        public int completeCond;
        public int[] completeCondParams;
        
        public QuestParams(int cond, int[] params) {
            this.completeCond = cond;
            this.completeCondParams = params;
        }
        
        public QuestParams(int cond, int param) {
            this(cond, new int[] {param});
        }
        
        public QuestParams(QuestCondType cond, int... params) {
            this(cond.getValue(), params);
        }
    }
}
