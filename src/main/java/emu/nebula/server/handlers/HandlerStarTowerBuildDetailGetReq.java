package emu.nebula.server.handlers;

import emu.nebula.net.NetHandler;
import emu.nebula.net.NetMsgId;
import emu.nebula.proto.StarTowerBuildDetailGet.StarTowerBuildDetailGetReq;
import emu.nebula.proto.StarTowerBuildDetailGet.StarTowerBuildDetailGetResp;
import emu.nebula.net.HandlerId;
import emu.nebula.net.GameSession;

@HandlerId(NetMsgId.star_tower_build_detail_get_req)
public class HandlerStarTowerBuildDetailGetReq extends NetHandler {

    @Override
    public byte[] handle(GameSession session, byte[] message) throws Exception {
        // Parse request
        var req = StarTowerBuildDetailGetReq.parseFrom(message);
        
        // Get build
        int buildId = (int) req.getBuildId();
        var build = session.getPlayer().getStarTowerManager().getBuilds().get(buildId);
        
        if (build == null) {
            return this.encodeMsg(NetMsgId.star_tower_build_detail_get_failed_ack);
        }
        
        // Build response
        var rsp = StarTowerBuildDetailGetResp.newInstance()
                .setDetail(build.toDetailProto());
        
        return this.encodeMsg(NetMsgId.star_tower_build_detail_get_succeed_ack, rsp);
    }

}
