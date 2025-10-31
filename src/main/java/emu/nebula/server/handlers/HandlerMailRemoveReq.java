package emu.nebula.server.handlers;

import emu.nebula.net.NetHandler;
import emu.nebula.net.NetMsgId;
import emu.nebula.proto.MailRemove.MailRemoveResp;
import emu.nebula.proto.Public.MailRequest;
import it.unimi.dsi.fastutil.ints.IntList;
import emu.nebula.net.HandlerId;
import emu.nebula.net.GameSession;

@HandlerId(NetMsgId.mail_remove_req)
public class HandlerMailRemoveReq extends NetHandler {

    @Override
    public byte[] handle(GameSession session, byte[] message) throws Exception {
        // Parse request
        var req = MailRequest.parseFrom(message);
        
        // Claim mail
        IntList removed = session.getPlayer().getMailbox().removeMail(session.getPlayer(), req.getId());
        
        // Build response
        var rsp = MailRemoveResp.newInstance();
        
        for (int id : removed) {
            rsp.addIds(id);
        }
        
        return session.encodeMsg(NetMsgId.mail_remove_succeed_ack, rsp);
    }

}
