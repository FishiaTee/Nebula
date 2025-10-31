package emu.nebula.server.handlers;

import emu.nebula.net.NetHandler;
import emu.nebula.net.NetMsgId;
import emu.nebula.proto.StorySetInfo.StorySetChapter;
import emu.nebula.proto.StorySetInfo.StorySetInfoResp;
import emu.nebula.net.HandlerId;
import emu.nebula.data.resources.StorySetSectionDef;
import emu.nebula.net.GameSession;

@HandlerId(NetMsgId.story_set_info_req)
public class HandlerStorySetInfoReq extends NetHandler {

    @Override
    public byte[] handle(GameSession session, byte[] message) throws Exception {
        // Create response
        var rsp = StorySetInfoResp.newInstance();
        
        // Get completed sets
        var completedSets = session.getPlayer().getStoryManager().getCompletedSets();
        
        // Story set (trekker story)
        for (int chapterId : StorySetSectionDef.getChapterIds()) {
            // Get completed index
            int index = completedSets.get(chapterId);
            
            var chapter = StorySetChapter.newInstance()
                    .setChapterId(chapterId)
                    .setSectionIndex(index);
            
            // Add rewarded ids
            if (index > 0) {
                for (int i = 1; i <= index; i++)
                chapter.addRewardedIds((chapterId * 100) + i);
            }
            
            rsp.addChapters(chapter);
        }
        
        return session.encodeMsg(NetMsgId.story_set_info_succeed_ack, rsp);
    }

}
