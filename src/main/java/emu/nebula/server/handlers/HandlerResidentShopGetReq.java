package emu.nebula.server.handlers;

import emu.nebula.net.NetHandler;
import emu.nebula.net.NetMsgId;
import emu.nebula.proto.Public.ResidentShop;
import emu.nebula.proto.ResidentShopGet.ResidentShopGetResp;
import emu.nebula.net.HandlerId;
import emu.nebula.data.GameData;
import emu.nebula.net.GameSession;

@HandlerId(NetMsgId.resident_shop_get_req)
public class HandlerResidentShopGetReq extends NetHandler {

    @Override
    public byte[] handle(GameSession session, byte[] message) throws Exception {
        // Build response
        var rsp = ResidentShopGetResp.newInstance();
        
        for (var data : GameData.getResidentShopDataTable()) {
            var proto = ResidentShop.newInstance()
                    .setId(data.getId())
                    .setRefreshTime(Long.MAX_VALUE);
            
            rsp.addShops(proto);
        }
        
        // Encode and send
        return session.encodeMsg(NetMsgId.resident_shop_get_succeed_ack, rsp);
    }

}
