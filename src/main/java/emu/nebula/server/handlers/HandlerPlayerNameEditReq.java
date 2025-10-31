package emu.nebula.server.handlers;

import emu.nebula.net.NetHandler;
import emu.nebula.net.NetMsgId;
import emu.nebula.proto.PlayerNameEdit.PlayerNameEditReq;
import emu.nebula.net.HandlerId;
import emu.nebula.net.GameSession;

@HandlerId(NetMsgId.player_name_edit_req)
public class HandlerPlayerNameEditReq extends NetHandler {

    @Override
    public byte[] handle(GameSession session, byte[] message) throws Exception {
        var req = PlayerNameEditReq.parseFrom(message);
        
        boolean success = session.getPlayer().editName(req.getName());
        
        return session.encodeMsg(success ? NetMsgId.player_name_edit_succeed_ack : NetMsgId.player_name_edit_failed_ack);
    }

}
