package emu.nebula.server.handlers;

import emu.nebula.net.NetHandler;
import emu.nebula.net.NetMsgId;
import emu.nebula.proto.StarTowerBuildWhetherSave.StarTowerBuildWhetherSaveReq;
import emu.nebula.proto.StarTowerBuildWhetherSave.StarTowerBuildWhetherSaveResp;
import emu.nebula.net.HandlerId;
import emu.nebula.net.GameSession;

@HandlerId(NetMsgId.star_tower_build_whether_save_req)
public class HandlerStarTowerBuildWhetherSaveReq extends NetHandler {

    @Override
    public byte[] handle(GameSession session, byte[] message) throws Exception {
        // Parse request
        var req = StarTowerBuildWhetherSaveReq.parseFrom(message);
        
        // Save build
        boolean result = session.getPlayer().getStarTowerManager().saveBuild(
                req.getDelete(), 
                req.getBuildName(), 
                req.getLock()
        );
        
        if (!result) {
            return this.encodeMsg(NetMsgId.star_tower_build_whether_save_failed_ack);
        }
        
        // Build response
        var rsp = StarTowerBuildWhetherSaveResp.newInstance();
        
        return this.encodeMsg(NetMsgId.star_tower_build_whether_save_succeed_ack, rsp);
    }

}
