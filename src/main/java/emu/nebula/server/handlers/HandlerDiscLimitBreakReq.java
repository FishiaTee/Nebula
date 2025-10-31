package emu.nebula.server.handlers;

import emu.nebula.net.NetHandler;
import emu.nebula.net.NetMsgId;
import emu.nebula.proto.DiscLimitBreak.DiscLimitBreakReq;
import emu.nebula.proto.DiscLimitBreak.DiscLimitBreakResp;
import emu.nebula.net.HandlerId;
import emu.nebula.net.GameSession;

@HandlerId(NetMsgId.disc_limit_break_req)
public class HandlerDiscLimitBreakReq extends NetHandler {

    @Override
    public byte[] handle(GameSession session, byte[] message) throws Exception {
        // Parse request
        var req = DiscLimitBreakReq.parseFrom(message);
        
        // Get character
        var disc = session.getPlayer().getCharacters().getDiscById(req.getId());
        
        if (disc == null) {
            return session.encodeMsg(NetMsgId.disc_limit_break_failed_ack);
        }
        
        // Limit break
        var change = disc.limitBreak(req.getQty());
        
        if (change == null) {
            return session.encodeMsg(NetMsgId.disc_limit_break_failed_ack);
        }
        
        // Create response
        var rsp = DiscLimitBreakResp.newInstance()
                .setStar(disc.getStar())
                .setChange(null);
        
        return session.encodeMsg(NetMsgId.disc_limit_break_succeed_ack, rsp);
    }

}
