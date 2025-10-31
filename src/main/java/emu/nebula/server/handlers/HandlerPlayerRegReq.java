package emu.nebula.server.handlers;

import emu.nebula.net.NetHandler;
import emu.nebula.net.NetMsgId;
import emu.nebula.proto.PlayerRegOuterClass.PlayerReg;
import emu.nebula.net.HandlerId;
import emu.nebula.Nebula;
import emu.nebula.game.player.Player;
import emu.nebula.net.GameSession;

@HandlerId(NetMsgId.player_reg_req)
public class HandlerPlayerRegReq extends NetHandler {

    public boolean requirePlayer() {
        return false;
    }
    
    @Override
    public byte[] handle(GameSession session, byte[] message) throws Exception {
        // Parse request
        var req = PlayerReg.parseFrom(message);
        
        // Sanity
        if (req.getNickname() == null || req.getNickname().isEmpty()) {
            return session.encodeMsg(NetMsgId.player_reg_failed_ack);
        }
        
        // Create player
        Player player = Nebula.getGameContext().getPlayerModule().createPlayer(session, req.getNickname(), req.getGender());
        
        if (player == null) {
            return session.encodeMsg(NetMsgId.player_reg_failed_ack);
        }
        
        // Encode player data
        return session.encodeMsg(NetMsgId.player_data_succeed_ack, session.getPlayer().toProto());
    }

}
