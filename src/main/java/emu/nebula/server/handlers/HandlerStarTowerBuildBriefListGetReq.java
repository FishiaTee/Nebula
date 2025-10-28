package emu.nebula.server.handlers;

import emu.nebula.net.NetHandler;
import emu.nebula.net.NetMsgId;
import emu.nebula.proto.StarTowerBuildBriefListGet.StarTowerBuildBriefListGetResp;
import emu.nebula.net.HandlerId;
import emu.nebula.net.GameSession;

@HandlerId(NetMsgId.star_tower_build_brief_list_get_req)
public class HandlerStarTowerBuildBriefListGetReq extends NetHandler {

    @Override
    public byte[] handle(GameSession session, byte[] message) throws Exception {
        // Build response
        var rsp = StarTowerBuildBriefListGetResp.newInstance();
        
        // Add star tower builds
        var builds = session.getPlayer().getStarTowerManager().getBuilds().values();
        for (var build : builds) {
            rsp.addBriefs(build.toBriefProto());
        }
        
        // Finish
        return this.encodeMsg(NetMsgId.star_tower_build_brief_list_get_succeed_ack, rsp);
    }

}
