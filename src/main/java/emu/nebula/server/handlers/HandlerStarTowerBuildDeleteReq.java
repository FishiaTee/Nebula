package emu.nebula.server.handlers;

import emu.nebula.net.NetHandler;
import emu.nebula.net.NetMsgId;
import emu.nebula.proto.StarTowerBuildDelete.StarTowerBuildDeleteReq;
import emu.nebula.proto.StarTowerBuildDelete.StarTowerBuildDeleteResp;
import emu.nebula.net.HandlerId;
import emu.nebula.game.player.PlayerChangeInfo;
import emu.nebula.net.GameSession;

@HandlerId(NetMsgId.star_tower_build_delete_req)
public class HandlerStarTowerBuildDeleteReq extends NetHandler {

    @Override
    public byte[] handle(GameSession session, byte[] message) throws Exception {
        // Parse request
        var req = StarTowerBuildDeleteReq.parseFrom(message);
        
        //
        var changes = new PlayerChangeInfo();
        
        // Delete
        for (var id : req.getBuildIds()) {
            session.getPlayer().getStarTowerManager().deleteBuild(id, changes);
        }
        
        // Build response
        var rsp = StarTowerBuildDeleteResp.newInstance()
                .setChange(changes.toProto());
                
        // Encode packet
        return session.encodeMsg(NetMsgId.star_tower_build_delete_succeed_ack, rsp);
    }

}
