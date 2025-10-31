package emu.nebula.server.handlers;

import emu.nebula.net.NetHandler;
import emu.nebula.net.NetMsgId;
import emu.nebula.proto.GachaInformation.GachaInformationResp;
import emu.nebula.net.HandlerId;
import emu.nebula.net.GameSession;

@HandlerId(NetMsgId.gacha_newbie_info_req)
public class HandlerGachaNewbieInfoReq extends NetHandler {

    @Override
    public byte[] handle(GameSession session, byte[] message) throws Exception {
        var rsp = GachaInformationResp.newInstance();
        
        return session.encodeMsg(NetMsgId.gacha_newbie_info_succeed_ack, rsp);
    }

}
