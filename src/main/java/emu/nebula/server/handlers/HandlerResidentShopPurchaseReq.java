package emu.nebula.server.handlers;

import emu.nebula.net.NetHandler;
import emu.nebula.net.NetMsgId;
import emu.nebula.proto.ResidentShopPurchase.ResidentShopPurchaseReq;
import emu.nebula.proto.ResidentShopPurchase.ResidentShopPurchaseResp;
import emu.nebula.net.HandlerId;
import emu.nebula.data.GameData;
import emu.nebula.net.GameSession;

@HandlerId(NetMsgId.resident_shop_purchase_req)
public class HandlerResidentShopPurchaseReq extends NetHandler {

    @Override
    public byte[] handle(GameSession session, byte[] message) throws Exception {
        // Parse request
        var req = ResidentShopPurchaseReq.parseFrom(message);
        
        // Get goods
        var data = GameData.getResidentGoodsDataTable().get(req.getGoodsId());
        if (data == null) {
            return session.encodeMsg(NetMsgId.resident_shop_purchase_failed_ack);
        }
        
        // Buy
        var change = session.getPlayer().getInventory().buyItem(
                data.getCurrencyItemId(),
                data.getPrice(),
                data.getProducts(),
                req.getNumber()
        );
        
        // Build response
        var rsp = ResidentShopPurchaseResp.newInstance()
                .setChange(change.toProto())
                .setPurchasedNumber(0); // Prevent avaliable item count from decreasing
        
        rsp.getMutableShop()
            .setId(data.getShopId())
            .setRefreshTime(Long.MAX_VALUE);
        
        // Encode and send
        return session.encodeMsg(NetMsgId.resident_shop_purchase_succeed_ack, rsp);
    }

}
