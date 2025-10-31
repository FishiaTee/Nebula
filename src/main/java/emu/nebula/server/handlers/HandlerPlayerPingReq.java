package emu.nebula.server.handlers;

import emu.nebula.net.NetHandler;
import emu.nebula.net.NetMsgId;
import emu.nebula.net.PacketHelper;
import emu.nebula.proto.PlayerPing.Pong;
import emu.nebula.proto.Public.MailState;
import emu.nebula.net.HandlerId;
import emu.nebula.Nebula;
import emu.nebula.net.GameSession;

@HandlerId(NetMsgId.player_ping_req)
public class HandlerPlayerPingReq extends NetHandler {

    @Override
    public byte[] handle(GameSession session, byte[] message) throws Exception {
        // Create response
        var rsp = Pong.newInstance()
                .setServerTs(Nebula.getCurrentTime());
        
        // Update mail state flag
        if (session.getPlayer().getMailbox().isNewState()) {
            // Clear
            session.getPlayer().getMailbox().clearNewState();
            
            // Send mail state notify
            byte[] nextPackage = PacketHelper.encodeMsg(
                    NetMsgId.mail_state_notify, 
                    MailState.newInstance().setNew(true)
            );
            
            rsp.setNextPackage(nextPackage);
        }
        
        return session.encodeMsg(NetMsgId.player_ping_succeed_ack, rsp);
    }

}
