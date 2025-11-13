package emu.nebula.game.quest;

public interface QuestData {
    
    public int getId();
    
    public int getQuestType();
    
    public int getCompleteCond();
    
    public int[] getCompleteCondParams();
    
}
