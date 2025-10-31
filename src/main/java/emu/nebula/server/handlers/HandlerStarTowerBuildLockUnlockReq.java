package emu.nebula.server.handlers;

import emu.nebula.net.NetHandler;
import emu.nebula.net.NetMsgId;
import emu.nebula.proto.StarTowerBuildLockUnlock.StarTowerBuildLockUnlockReq;
import emu.nebula.net.HandlerId;
import emu.nebula.net.GameSession;

@HandlerId(NetMsgId.star_tower_build_lock_unlock_req)
public class HandlerStarTowerBuildLockUnlockReq extends NetHandler {

    @Override
    public byte[] handle(GameSession session, byte[] message) throws Exception {
        // Parse request
        var req = StarTowerBuildLockUnlockReq.parseFrom(message);
        
        // Get build
        var build = session.getPlayer().getStarTowerManager().getBuildById(req.getBuildId());
        
        if (build == null) {
            return session.encodeMsg(NetMsgId.star_tower_build_lock_unlock_failed_ack);
        }
        
        build.setLock(req.getLock());
        
        // Encode packet
        return session.encodeMsg(NetMsgId.star_tower_build_lock_unlock_succeed_ack);
    }

}
