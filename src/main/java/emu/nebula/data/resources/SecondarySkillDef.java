package emu.nebula.data.resources;

import java.util.ArrayList;
import java.util.List;

import emu.nebula.data.BaseDef;
import emu.nebula.data.GameData;
import emu.nebula.data.ResourceType;
import emu.nebula.game.inventory.ItemParamMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.Getter;

@Getter
@ResourceType(name = "SecondarySkill.json")
public class SecondarySkillDef extends BaseDef {
    private int Id;
    private int GroupId;
    private int Score;
    private String NeedSubNoteSkills;
    
    private transient ItemParamMap reqSubNotes;
    
    @Getter
    private static transient Int2ObjectMap<List<SecondarySkillDef>> groups = new Int2ObjectOpenHashMap<>();
    
    @Override
    public int getId() {
        return Id;
    }
    
    public boolean match(ItemParamMap subNotes) {
        for (var item : this.reqSubNotes) {
            int reqId = item.getIntKey();
            int reqCount = item.getIntValue();
            
            int curCount = subNotes.get(reqId);
            if (curCount < reqCount) {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public void onLoad() {
        // Setup required subnotes
        this.reqSubNotes = ItemParamMap.fromJsonString(this.NeedSubNoteSkills);
        
        // Add to group cache
        var group = groups.computeIfAbsent(this.GroupId, id -> new ArrayList<>());
        group.add(this);
        
        // Clear to save memory
        this.NeedSubNoteSkills = null;
    }
    
    // Static sub note skill group group

    public static List<SecondarySkillDef> getGroup(int id) {
        return groups.get(id);
    }
    
    public static IntSet calculateSecondarySkills(int[] discIds, ItemParamMap subNotes) {
        var secondarySkills = new IntOpenHashSet();
        
        // Get first 3 discs
        for (int i = 0; i < 3; i++) {
            // Disc id
            int discId = discIds[i];
            
            // Get disc data
            var data = GameData.getDiscDataTable().get(discId);
            if (data == null) continue;
            
            // Add secondary skills
            int s1= getSecondarySkill(subNotes, data.getSecondarySkillGroupId1());
            if (s1 > 0) {
                secondarySkills.add(s1);
            }
            
            int s2 = getSecondarySkill(subNotes, data.getSecondarySkillGroupId2());
            if (s2 > 0) {
                secondarySkills.add(s2);
            }
        }
        
        return secondarySkills;
    }
    
    private static int getSecondarySkill(ItemParamMap subNotes, int groupId) {
        // Check group id
        if (groupId <= 0) {
            return 0;
        }
        
        // Get group
        var group = SecondarySkillDef.getGroup(groupId);
        if (group == null) {
            return 0;
        }
        
        // Reverse iterator to try and match highest secondary skill first
        for (int i = group.size() - 1; i >= 0; i--) {
            var data = group.get(i);
            
            if (data.match(subNotes)) {
                return data.getId();
            }
        }
        
        // Failure
        return 0;
    }
}
