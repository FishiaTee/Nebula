package emu.nebula.server.handlers;

import emu.nebula.net.NetHandler;
import emu.nebula.net.NetMsgId;
import emu.nebula.proto.PlayerTitleEdit.PlayerTitleEditReq;
import emu.nebula.net.HandlerId;
import emu.nebula.net.GameSession;

@HandlerId(NetMsgId.player_title_edit_req)
public class HandlerPlayerTitleEditReq extends NetHandler {

    @Override
    public byte[] handle(GameSession session, byte[] message) throws Exception {
        // Parse request
        var req = PlayerTitleEditReq.parseFrom(message);
        
        boolean success = session.getPlayer().editTitle(req.getTitlePrefix(), req.getTitleSuffix());
        
        return this.encodeMsg(success ? NetMsgId.player_title_edit_succeed_ack : NetMsgId.player_title_edit_failed_ack);
    }

}
