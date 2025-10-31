package emu.nebula.server.handlers;

import emu.nebula.net.NetHandler;
import emu.nebula.net.NetMsgId;
import emu.nebula.proto.RegionBossLevelSettle.RegionBossLevelSettleReq;
import emu.nebula.proto.RegionBossLevelSettle.RegionBossLevelSettleResp;
import emu.nebula.net.HandlerId;
import emu.nebula.data.GameData;
import emu.nebula.game.instance.InstanceSettleData;
import emu.nebula.net.GameSession;

@HandlerId(NetMsgId.region_boss_level_settle_req)
public class HandlerRegionBossLevelSettleReq extends NetHandler {

    @Override
    public byte[] handle(GameSession session, byte[] message) throws Exception {
        // Cache player
        var player = session.getPlayer();
        
        // Get boss level data
        var data = GameData.getRegionBossLevelDataTable().get(player.getInstanceManager().getCurInstanceId());
        if (data == null || !data.hasEnergy(player)) {
            return session.encodeMsg(NetMsgId.region_boss_level_settle_failed_ack);
        }
        
        // Parse request
        var req = RegionBossLevelSettleReq.parseFrom(message);
        
        // Settle instance
        var changes = player.getInstanceManager().settleInstance(
                data,
                player.getInstanceManager().getRegionBossLog(),
                "regionBossLog",
                req.getStar()
        );
        
        var settleData = (InstanceSettleData) changes.getExtraData();
        
        // Create response
        var rsp = RegionBossLevelSettleResp.newInstance()
                .setExp(settleData.getExp())
                .setThreeStar(req.getStar() == 7)
                .setChange(changes.toProto());
        
        // Add reward items
        if (settleData.isWin()) {
            data.getRewards().toItemTemplateStream().forEach(rsp::addAwardItems);
            
            if (settleData.isFirst()) {
                data.getFirstRewards().toItemTemplateStream().forEach(rsp::addAwardItems);
            }
        }
        
        // Send response
        return session.encodeMsg(NetMsgId.region_boss_level_settle_succeed_ack, rsp);
    }

}
