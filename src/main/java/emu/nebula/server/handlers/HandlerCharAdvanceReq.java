package emu.nebula.server.handlers;

import emu.nebula.net.NetHandler;
import emu.nebula.net.NetMsgId;
import emu.nebula.proto.Public.UI32;
import emu.nebula.net.HandlerId;
import emu.nebula.net.GameSession;

@HandlerId(NetMsgId.char_advance_req)
public class HandlerCharAdvanceReq extends NetHandler {

    @Override
    public byte[] handle(GameSession session, byte[] message) throws Exception {
        var req = UI32.parseFrom(message);
        
        // Get character
        var character = session.getPlayer().getCharacters().getCharacterById(req.getValue());
        
        if (character == null) {
            return session.encodeMsg(NetMsgId.char_advance_failed_ack);
        }
        
        // Advance character
        var change = character.advance();
        
        if (change == null) {
            return session.encodeMsg(NetMsgId.char_advance_failed_ack);
        }
        
        return session.encodeMsg(NetMsgId.char_advance_succeed_ack, change.toProto());
    }

}
