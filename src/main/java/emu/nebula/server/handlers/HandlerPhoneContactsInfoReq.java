package emu.nebula.server.handlers;

import emu.nebula.net.NetHandler;
import emu.nebula.net.NetMsgId;
import emu.nebula.net.HandlerId;
import emu.nebula.net.GameSession;

@HandlerId(NetMsgId.phone_contacts_info_req)
public class HandlerPhoneContactsInfoReq extends NetHandler {

    @Override
    public byte[] handle(GameSession session, byte[] message) throws Exception {
        return session.encodeMsg(NetMsgId.phone_contacts_info_succeed_ack);
    }

}
