package emu.nebula.server.handlers;

import emu.nebula.net.NetHandler;
import emu.nebula.net.NetMsgId;
import emu.nebula.proto.GachaNewbieObtain.GachaNewbieObtainReq;
import emu.nebula.net.HandlerId;
import emu.nebula.net.GameSession;

@HandlerId(NetMsgId.gacha_newbie_obtain_req)
public class HandlerGachaNewbieObtainReq extends NetHandler {

    @Override
    public byte[] handle(GameSession session, byte[] message) throws Exception {
        @SuppressWarnings("unused")
        var req = GachaNewbieObtainReq.parseFrom(message);
        
        // TODO
        return session.encodeMsg(NetMsgId.gacha_newbie_obtain_failed_ack);
    }

}
