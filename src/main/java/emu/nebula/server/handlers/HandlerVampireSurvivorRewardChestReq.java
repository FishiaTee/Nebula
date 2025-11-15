package emu.nebula.server.handlers;

import emu.nebula.net.NetHandler;
import emu.nebula.net.NetMsgId;
import emu.nebula.proto.Public.CardInfo;
import emu.nebula.proto.VampireSurvivorRewardChest.VampireSurvivorRewardChestReq;
import emu.nebula.proto.VampireSurvivorRewardChest.VampireSurvivorRewardChestResp;
import emu.nebula.net.HandlerId;
import emu.nebula.net.GameSession;

@HandlerId(NetMsgId.vampire_survivor_reward_chest_req)
public class HandlerVampireSurvivorRewardChestReq extends NetHandler {

    @Override
    public byte[] handle(GameSession session, byte[] message) throws Exception {
        // Parse req
        var req = VampireSurvivorRewardChestReq.parseFrom(message);
        
        // Sanity check
        var game = session.getPlayer().getVampireSurvivorManager().getGame();
        if (game == null) {
            return session.encodeMsg(NetMsgId.vampire_survivor_reward_chest_failed_ack);
        }
        
        // Calculate rewards from chest
        var chest = game.calcRewardChest(req.getEventType(), req.getNumber());
        
        // Build response
        var rsp = VampireSurvivorRewardChestResp.newInstance();
        
        for (int cardId : chest) {
            var card = CardInfo.newInstance()
                    .setId(cardId)
                    .setNew(game.isNewCard(cardId));
            
            rsp.addChestCards(card);
        }
        
        // Encode and send
        return session.encodeMsg(NetMsgId.vampire_survivor_reward_chest_succeed_ack, rsp);
    }

}
