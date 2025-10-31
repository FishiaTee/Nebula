package emu.nebula.server.handlers;

import emu.nebula.net.NetHandler;
import emu.nebula.net.NetMsgId;
import emu.nebula.proto.MailRecv.MailRecvResp;
import emu.nebula.proto.Public.MailRequest;
import it.unimi.dsi.fastutil.ints.IntList;
import emu.nebula.net.HandlerId;
import emu.nebula.game.player.PlayerChangeInfo;
import emu.nebula.net.GameSession;

@HandlerId(NetMsgId.mail_recv_req)
public class HandlerMailRecvReq extends NetHandler {

    @Override
    public byte[] handle(GameSession session, byte[] message) throws Exception {
        // Parse request
        var req = MailRequest.parseFrom(message);
        
        // Claim mail
        PlayerChangeInfo changes = session.getPlayer().getMailbox().recvMail(session.getPlayer(), req.getId());
        
        // Build response
        var rsp = MailRecvResp.newInstance()
                .setItems(changes.toProto());
        
        // Add mail ids that we received
        if (changes.getExtraData() != null) {
            var recvList = (IntList) changes.getExtraData();
            recvList.forEach(rsp::addIds);
        }
        
        return session.encodeMsg(NetMsgId.mail_recv_succeed_ack, rsp);
    }

}
