package emu.nebula.server.handlers;

import emu.nebula.net.NetHandler;
import emu.nebula.net.NetMsgId;
import emu.nebula.proto.PlayerBoard.PlayerBoardSetReq;
import emu.nebula.net.HandlerId;
import emu.nebula.net.GameSession;

@HandlerId(NetMsgId.player_board_set_req)
public class HandlerPlayerBoardSetReq extends NetHandler {

    @Override
    public byte[] handle(GameSession session, byte[] message) throws Exception {
        // Parse request
        var req = PlayerBoardSetReq.parseFrom(message);
        
        // Set board
        boolean success = session.getPlayer().setBoard(req.getIds());
        
        return session.encodeMsg(success ? NetMsgId.player_board_set_succeed_ack : NetMsgId.player_board_set_failed_ack);
    }

}
