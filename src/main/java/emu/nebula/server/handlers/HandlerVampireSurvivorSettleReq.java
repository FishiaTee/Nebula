package emu.nebula.server.handlers;

import emu.nebula.net.NetHandler;
import emu.nebula.net.NetMsgId;
import emu.nebula.proto.VampireSurvivorSettle.VampireSurvivorAreaInfo;
import emu.nebula.proto.VampireSurvivorSettle.VampireSurvivorSettleReq;
import emu.nebula.proto.VampireSurvivorSettle.VampireSurvivorSettleResp;
import emu.nebula.net.HandlerId;
import emu.nebula.net.GameSession;

@HandlerId(NetMsgId.vampire_survivor_settle_req)
public class HandlerVampireSurvivorSettleReq extends NetHandler {

    @Override
    public byte[] handle(GameSession session, byte[] message) throws Exception {
        // Parse request
        var req = VampireSurvivorSettleReq.parseFrom(message);
        
        // Sanity check
        var game = session.getPlayer().getVampireSurvivorManager().getGame();
        
        if (game == null) {
            session.encodeMsg(NetMsgId.vampire_survivor_settle_failed_ack);
        }
        
        // Calculate victory + score
        boolean victory = !req.getDefeat();
        int score = 1;
        
        // Settle
        session.getPlayer().getVampireSurvivorManager().settle(victory, score);
        
        // Build response
        var rsp = VampireSurvivorSettleResp.newInstance();
        
        if (victory) {
            var areaInfo = VampireSurvivorAreaInfo.newInstance()
                    .setBossTime(1)
                    .setScore(score);
            
            // TODO
            for (int i : req.getKillCount()) {
                areaInfo.addKillCount(i);
                areaInfo.addKillScore(0);
            }
            
            rsp.getMutableVictory()
                .setFinalScore(score)
                .addInfos(areaInfo);
        } else {
            rsp.getMutableDefeat()
                .setFinalScore(score);
        }
        
        // Encode and send
        return session.encodeMsg(NetMsgId.vampire_survivor_settle_succeed_ack, rsp);
    }
}
