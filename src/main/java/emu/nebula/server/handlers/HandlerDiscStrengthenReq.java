package emu.nebula.server.handlers;

import emu.nebula.net.NetHandler;
import emu.nebula.net.NetMsgId;
import emu.nebula.proto.DiscStrengthen.DiscStrengthenReq;
import emu.nebula.proto.DiscStrengthen.DiscStrengthenResp;
import emu.nebula.net.HandlerId;
import emu.nebula.game.inventory.ItemParamMap;
import emu.nebula.net.GameSession;

@HandlerId(NetMsgId.disc_strengthen_req)
public class HandlerDiscStrengthenReq extends NetHandler {

    @Override
    public byte[] handle(GameSession session, byte[] message) throws Exception {
        // Parse request
        var req = DiscStrengthenReq.parseFrom(message);
        
        // Get character
        var disc = session.getPlayer().getCharacters().getDiscById(req.getId());
        
        if (disc == null) {
            return session.encodeMsg(NetMsgId.disc_strengthen_failed_ack);
        }
        
        // Level up disc
        var params = ItemParamMap.fromItemInfos(req.getItems());
        var change = disc.upgrade(params);
        
        if (change == null) {
            return session.encodeMsg(NetMsgId.disc_strengthen_failed_ack);
        }
        
        // Create response
        var rsp = DiscStrengthenResp.newInstance()
                .setLevel(disc.getLevel())
                .setExp(disc.getExp())
                .setChange(change.toProto());
        
        return session.encodeMsg(NetMsgId.disc_strengthen_succeed_ack, rsp);
    }

}
