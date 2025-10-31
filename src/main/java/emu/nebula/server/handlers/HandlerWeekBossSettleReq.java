package emu.nebula.server.handlers;

import emu.nebula.net.NetHandler;
import emu.nebula.net.NetMsgId;
import emu.nebula.proto.WeekBossSettle.WeekBossLevelSettleResp;
import emu.nebula.proto.WeekBossSettle.WeekBossSettleReq;
import emu.nebula.net.HandlerId;
import emu.nebula.data.GameData;
import emu.nebula.game.instance.InstanceSettleData;
import emu.nebula.net.GameSession;

@HandlerId(NetMsgId.week_boss_settle_req)
public class HandlerWeekBossSettleReq extends NetHandler {

    @Override
    public byte[] handle(GameSession session, byte[] message) throws Exception {
        // Cache player
        var player = session.getPlayer();
        
        // Get boss level data
        var data = GameData.getWeekBossLevelDataTable().get(player.getInstanceManager().getCurInstanceId());
        if (data == null || !data.hasEnergy(player)) {
            return session.encodeMsg(NetMsgId.week_boss_settle_failed_ack);
        }
        
        // Parse request
        var req = WeekBossSettleReq.parseFrom(message);
        
        // Settle instance
        var changes = player.getInstanceManager().settleInstance(
                data,
                player.getInstanceManager().getWeekBossLog(),
                "weekBossLog",
                req.getResult() ? 1 : 0
        );
        
        var settleData = (InstanceSettleData) changes.getExtraData();
        
        // Create response
        var rsp = WeekBossLevelSettleResp.newInstance()
                .setFirst(settleData.isFirst())
                .setChange(changes.toProto());
        
        // Add reward items
        if (settleData.isWin()) {
            data.getRewards().toItemTemplateStream().forEach(rsp::addAwardItems);
            
            if (settleData.isFirst()) {
                data.getFirstRewards().toItemTemplateStream().forEach(rsp::addAwardItems);
            }
        }
        
        // Send response
        return session.encodeMsg(NetMsgId.week_boss_settle_succeed_ack, rsp);
    }

}
