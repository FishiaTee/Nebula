package emu.nebula.server.handlers;

import emu.nebula.net.NetHandler;
import emu.nebula.net.NetMsgId;
import emu.nebula.net.HandlerId;
import emu.nebula.net.GameSession;

@HandlerId(NetMsgId.player_gender_edit_req)
public class HandlerPlayerGenderEditReq extends NetHandler {

    @Override
    public byte[] handle(GameSession session, byte[] message) throws Exception {
        session.getPlayer().editGender();
        
        return session.encodeMsg(NetMsgId.player_gender_edit_succeed_ack);
    }

}
