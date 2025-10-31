package emu.nebula.server.handlers;

import emu.nebula.net.NetHandler;
import emu.nebula.net.NetMsgId;
import emu.nebula.proto.ItemProduct.ItemProductReq;
import emu.nebula.net.HandlerId;
import emu.nebula.net.GameSession;

@HandlerId(NetMsgId.item_product_req)
public class HandlerItemProductReq extends NetHandler {

    @Override
    public byte[] handle(GameSession session, byte[] message) throws Exception {
        // Parse request
        var req = ItemProductReq.parseFrom(message);
        
        // Produce item
        var changes = session.getPlayer().getInventory().produce(req.getId(), req.getNum());
        
        if (changes == null) {
            return session.encodeMsg(NetMsgId.item_product_failed_ack);
        }
        
        // Send response
        return session.encodeMsg(NetMsgId.item_product_succeed_ack, changes.toProto());
    }

}
