package emu.nebula.server.handlers;

import emu.nebula.net.NetHandler;
import emu.nebula.net.NetMsgId;
import emu.nebula.proto.Public.NewbieInfo;
import emu.nebula.net.HandlerId;
import emu.nebula.net.GameSession;

@HandlerId(NetMsgId.player_learn_req)
public class HandlerPlayerLearnReq extends NetHandler {

    @Override
    public byte[] handle(GameSession session, byte[] message) throws Exception {
        var req = NewbieInfo.parseFrom(message);
        
        // TODO set newbie info
        session.getPlayer().setNewbieInfo(req.getGroupId(), req.getStepId());
        
        return session.encodeMsg(NetMsgId.player_learn_succeed_ack);
    }

}
