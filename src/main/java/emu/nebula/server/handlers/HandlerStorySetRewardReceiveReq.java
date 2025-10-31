package emu.nebula.server.handlers;

import emu.nebula.net.NetHandler;
import emu.nebula.net.NetMsgId;
import emu.nebula.proto.StorySetRewardReceive.StorySetRewardReceiveReq;
import emu.nebula.net.HandlerId;
import emu.nebula.net.GameSession;

@HandlerId(NetMsgId.story_set_reward_receive_req)
public class HandlerStorySetRewardReceiveReq extends NetHandler {

    @Override
    public byte[] handle(GameSession session, byte[] message) throws Exception {
        // Parse request
        var req = StorySetRewardReceiveReq.parseFrom(message);
        
        // Settle
        var changes = session.getPlayer().getStoryManager().settleSet(req.getChapterId(), req.getSectionId());
        
        // Finish
        return session.encodeMsg(NetMsgId.story_set_reward_receive_succeed_ack, changes.toProto());
    }

}
