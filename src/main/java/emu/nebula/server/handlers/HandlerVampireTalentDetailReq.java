package emu.nebula.server.handlers;

import emu.nebula.net.NetHandler;
import emu.nebula.net.NetMsgId;
import emu.nebula.proto.VampireTalentDetail.VampireTalentDetailResp;
import emu.nebula.net.HandlerId;

import emu.nebula.net.GameSession;

@HandlerId(NetMsgId.vampire_talent_detail_req)
public class HandlerVampireTalentDetailReq extends NetHandler {

    @Override
    public byte[] handle(GameSession session, byte[] message) throws Exception {
        // Get vampire surv manager
        var manager = session.getPlayer().getVampireSurvivorManager();
        
        // Build response
        var rsp = VampireTalentDetailResp.newInstance()
                .setNodes(manager.getTalents().toByteArray())
                .setActiveCount(manager.getTalentPoints());
        
        // Encode and send
        return session.encodeMsg(NetMsgId.vampire_talent_detail_succeed_ack, rsp);
    }

}
