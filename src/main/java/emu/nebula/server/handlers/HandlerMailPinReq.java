package emu.nebula.server.handlers;

import emu.nebula.net.NetHandler;
import emu.nebula.net.NetMsgId;
import emu.nebula.proto.MailPin.MailPinRequest;
import emu.nebula.net.HandlerId;
import emu.nebula.game.mail.GameMail;
import emu.nebula.net.GameSession;

@HandlerId(NetMsgId.mail_pin_req)
public class HandlerMailPinReq extends NetHandler {

    @Override
    public byte[] handle(GameSession session, byte[] message) throws Exception {
        // Parse request
        var req = MailPinRequest.parseFrom(message);
        
        // Pin mail
        GameMail mail = session.getPlayer().getMailbox().pinMail(req.getId(), req.getFlag(), req.hasPin());
        
        // Sanity check
        if (mail == null) {
            return session.encodeMsg(NetMsgId.mail_pin_failed_ack);
        }
        
        // Build response
        var rsp = MailPinRequest.newInstance()
                .setId(mail.getId())
                .setFlag(mail.getFlag())
                .setPin(mail.isPin());
        
        return session.encodeMsg(NetMsgId.mail_pin_succeed_ack, rsp);
    }

}
