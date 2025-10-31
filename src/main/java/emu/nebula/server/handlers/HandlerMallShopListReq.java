package emu.nebula.server.handlers;

import emu.nebula.net.NetHandler;
import emu.nebula.net.NetMsgId;
import emu.nebula.proto.MallShopList.MallShopProductList;
import emu.nebula.proto.MallShopList.ProductInfo;
import emu.nebula.net.HandlerId;

import java.util.concurrent.TimeUnit;

import emu.nebula.Nebula;
import emu.nebula.data.GameData;
import emu.nebula.net.GameSession;

@HandlerId(NetMsgId.mall_shop_list_req)
public class HandlerMallShopListReq extends NetHandler {

    @Override
    public byte[] handle(GameSession session, byte[] message) throws Exception {
        var rsp = MallShopProductList.newInstance();
        
        long refreshTime = Nebula.getCurrentTime() + TimeUnit.DAYS.toSeconds(30);
        
        for (var data : GameData.getMallShopDataTable()) {
            if (data.getStock() <= 0) {
                continue;
            }
            
            var info = ProductInfo.newInstance()
                    .setId(data.getIdString())
                    .setStock(data.getStock())
                    .setRefreshTime(refreshTime);
            
            rsp.addList(info);
        }
        
        return session.encodeMsg(NetMsgId.mall_shop_list_succeed_ack, rsp);
    }

}
