package emu.nebula.server.handlers;

import emu.nebula.net.NetHandler;
import emu.nebula.net.NetMsgId;
import emu.nebula.proto.StarTowerApply.StarTowerApplyReq;
import emu.nebula.proto.StarTowerApply.StarTowerApplyResp;
import emu.nebula.net.HandlerId;
import emu.nebula.net.GameSession;

@HandlerId(NetMsgId.star_tower_apply_req)
public class HandlerStarTowerApplyReq extends NetHandler {

    @Override
    public byte[] handle(GameSession session, byte[] message) throws Exception {
        // Parse req
        var req = StarTowerApplyReq.parseFrom(message);
        
        // Apply to create a star tower instance
        var instance = session.getPlayer().getStarTowerManager().apply(req);
        
        if (instance == null) {
            return session.encodeMsg(NetMsgId.star_tower_apply_failed_ack);
        }
        
        // Create response
        var rsp = StarTowerApplyResp.newInstance()
                .setLastId(req.getId())
                .setInfo(instance.toProto());
        
        rsp.getMutableChange();
        
        return session.encodeMsg(NetMsgId.star_tower_apply_succeed_ack, rsp);
    }

}
