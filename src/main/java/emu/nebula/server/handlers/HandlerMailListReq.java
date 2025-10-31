package emu.nebula.server.handlers;

import emu.nebula.net.NetHandler;
import emu.nebula.net.NetMsgId;
import emu.nebula.proto.Public.Mails;
import emu.nebula.net.HandlerId;
import emu.nebula.net.GameSession;

@HandlerId(NetMsgId.mail_list_req)
public class HandlerMailListReq extends NetHandler {

    @Override
    public byte[] handle(GameSession session, byte[] message) throws Exception {
        // Build mail list proto
        var rsp = Mails.newInstance();
        
        for (var mail : session.getPlayer().getMailbox()) {
            rsp.addList(mail.toProto());
        }
        
        return session.encodeMsg(NetMsgId.mail_list_succeed_ack, rsp);
    }

}
