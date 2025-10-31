package emu.nebula.server.handlers;

import emu.nebula.net.NetHandler;
import emu.nebula.net.NetMsgId;
import emu.nebula.proto.StarTowerBuildNameSet.StarTowerBuildNameSetReq;
import emu.nebula.net.HandlerId;
import emu.nebula.net.GameSession;

@HandlerId(NetMsgId.star_tower_build_name_set_req)
public class HandlerStarTowerBuildNameSetReq extends NetHandler {

    @Override
    public byte[] handle(GameSession session, byte[] message) throws Exception {
        // Parse request
        var req = StarTowerBuildNameSetReq.parseFrom(message);
        
        // Sanity
        if (req.getName() == null || req.getName().isEmpty()) {
            return session.encodeMsg(NetMsgId.star_tower_build_name_set_failed_ack);
        }
        
        // Get build
        var build = session.getPlayer().getStarTowerManager().getBuildById(req.getBuildId());
        
        if (build == null) {
            return session.encodeMsg(NetMsgId.star_tower_build_name_set_failed_ack);
        }
        
        // Set name
        build.setName(req.getName());
        
        // Encode packet
        return session.encodeMsg(NetMsgId.star_tower_build_name_set_succeed_ack);
    }

}
