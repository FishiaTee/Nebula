package emu.nebula.server.handlers;

import emu.nebula.net.NetHandler;
import emu.nebula.net.NetMsgId;
import emu.nebula.proto.Public.MailRequest;
import emu.nebula.proto.Public.UI32;
import emu.nebula.net.HandlerId;
import emu.nebula.net.GameSession;

@HandlerId(NetMsgId.mail_read_req)
public class HandlerMailReadReq extends NetHandler {

    @Override
    public byte[] handle(GameSession session, byte[] message) throws Exception {
        var req = MailRequest.parseFrom(message);
        
        boolean result = session.getPlayer().getMailbox().readMail(req.getId(), req.getFlag());
        
        if (!result) {
            return session.encodeMsg(NetMsgId.mail_read_failed_ack);
        }
        
        return session.encodeMsg(NetMsgId.mail_read_succeed_ack, UI32.newInstance().setValue(req.getId()));
    }

}
