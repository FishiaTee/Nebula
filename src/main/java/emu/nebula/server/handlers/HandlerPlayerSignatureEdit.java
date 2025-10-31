package emu.nebula.server.handlers;

import emu.nebula.net.NetHandler;
import emu.nebula.net.NetMsgId;
import emu.nebula.proto.PlayerSignatureEdit.PlayerSignatureEditReq;
import emu.nebula.net.HandlerId;
import emu.nebula.net.GameSession;

@HandlerId(NetMsgId.player_signature_edit_req)
public class HandlerPlayerSignatureEdit extends NetHandler {

    @Override
    public byte[] handle(GameSession session, byte[] message) throws Exception {
        // Parse request
        var req = PlayerSignatureEditReq.parseFrom(message);
        
        session.getPlayer().editSignature(req.getSignature());
        
        // Send response
        return session.encodeMsg(NetMsgId.player_signature_edit_succeed_ack);
    }

}
