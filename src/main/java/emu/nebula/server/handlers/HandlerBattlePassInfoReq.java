package emu.nebula.server.handlers;

import emu.nebula.net.NetHandler;
import emu.nebula.net.NetMsgId;
import emu.nebula.net.HandlerId;
import emu.nebula.net.GameSession;

@HandlerId(NetMsgId.battle_pass_info_req)
public class HandlerBattlePassInfoReq extends NetHandler {

    @Override
    public byte[] handle(GameSession session, byte[] message) throws Exception {
        // Get battle pass proto
        var info = session.getPlayer().getBattlePassManager().getBattlePass().toProto();
        
        // Encode and send
        return session.encodeMsg(NetMsgId.battle_pass_info_succeed_ack, info);
    }

}
