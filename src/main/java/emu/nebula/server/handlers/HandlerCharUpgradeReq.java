package emu.nebula.server.handlers;

import emu.nebula.net.NetHandler;
import emu.nebula.net.NetMsgId;
import emu.nebula.proto.CharUpgrade.CharUpgradeReq;
import emu.nebula.proto.CharUpgrade.CharUpgradeResp;
import emu.nebula.net.HandlerId;
import emu.nebula.game.inventory.ItemParamMap;
import emu.nebula.net.GameSession;

@HandlerId(NetMsgId.char_upgrade_req)
public class HandlerCharUpgradeReq extends NetHandler {

    @Override
    public byte[] handle(GameSession session, byte[] message) throws Exception {
        // Parse request
        var req = CharUpgradeReq.parseFrom(message);
        
        // Get character
        var character = session.getPlayer().getCharacters().getCharacterById(req.getCharId());
        
        if (character == null) {
            return session.encodeMsg(NetMsgId.char_upgrade_failed_ack);
        }
        
        // Upgrade character
        var params = ItemParamMap.fromTemplates(req.getItems());
        var change = character.upgrade(params);
        
        if (change == null) {
            return session.encodeMsg(NetMsgId.char_upgrade_failed_ack);
        }
        
        // Create response
        var rsp = CharUpgradeResp.newInstance()
                .setChange(change.toProto())
                .setLevel(character.getLevel())
                .setExp(character.getExp());
        
        return session.encodeMsg(NetMsgId.char_upgrade_succeed_ack, rsp);
    }

}
