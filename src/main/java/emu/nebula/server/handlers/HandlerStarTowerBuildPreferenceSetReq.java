package emu.nebula.server.handlers;

import emu.nebula.net.NetHandler;
import emu.nebula.net.NetMsgId;
import emu.nebula.proto.StarTowerBuildPreferenceSet.StarTowerBuildPreferenceSetReq;
import emu.nebula.net.HandlerId;
import emu.nebula.net.GameSession;

@HandlerId(NetMsgId.star_tower_build_preference_set_req)
public class HandlerStarTowerBuildPreferenceSetReq extends NetHandler {

    @Override
    public byte[] handle(GameSession session, byte[] message) throws Exception {
        // Parse request
        var req = StarTowerBuildPreferenceSetReq.parseFrom(message);
        
        for (long id : req.getCheckInIds()) {
            // Get build
            var build = session.getPlayer().getStarTowerManager().getBuildById(id);
            
            if (build == null) {
                continue;
            }
            
            build.setPreference(true);
        }
        
        for (long id : req.getCheckOutIds()) {
            // Get build
            var build = session.getPlayer().getStarTowerManager().getBuildById(id);
            
            if (build == null) {
                continue;
            }
            
            build.setPreference(false);
        }
        
        // Encode packet
        return session.encodeMsg(NetMsgId.star_tower_build_preference_set_succeed_ack);
    }

}
