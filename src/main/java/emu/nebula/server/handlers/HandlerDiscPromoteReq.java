package emu.nebula.server.handlers;

import emu.nebula.net.NetHandler;
import emu.nebula.net.NetMsgId;
import emu.nebula.proto.DiscPromote.DiscPromoteReq;
import emu.nebula.proto.DiscPromote.DiscPromoteResp;
import emu.nebula.net.HandlerId;
import emu.nebula.net.GameSession;

@HandlerId(NetMsgId.disc_promote_req)
public class HandlerDiscPromoteReq extends NetHandler {

    @Override
    public byte[] handle(GameSession session, byte[] message) throws Exception {
        // Parse request
        var req = DiscPromoteReq.parseFrom(message);
        
        // Get character
        var disc = session.getPlayer().getCharacters().getDiscById(req.getId());
        
        if (disc == null) {
            return session.encodeMsg(NetMsgId.disc_promote_failed_ack);
        }
        
        // Advance character
        var change = disc.promote();
        
        if (change == null) {
            return session.encodeMsg(NetMsgId.disc_promote_failed_ack);
        }
        
        // Build request
        var rsp = DiscPromoteResp.newInstance()
                .setPhase(disc.getPhase())
                .setChange(change.toProto());
        
        return session.encodeMsg(NetMsgId.disc_promote_succeed_ack, rsp);
    }

}
