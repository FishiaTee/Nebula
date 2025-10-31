package emu.nebula.server.handlers;

import emu.nebula.net.NetHandler;
import emu.nebula.net.NetMsgId;
import emu.nebula.proto.CharSkillUpgrade.CharSkillUpgradeReq;
import emu.nebula.net.HandlerId;
import emu.nebula.net.GameSession;

@HandlerId(NetMsgId.char_skill_upgrade_req)
public class HandlerCharSkillUpgradeReq extends NetHandler {

    @Override
    public byte[] handle(GameSession session, byte[] message) throws Exception {
        var req = CharSkillUpgradeReq.parseFrom(message);
        
        // Get character
        var character = session.getPlayer().getCharacters().getCharacterById(req.getCharId());
        
        if (character == null) {
            return session.encodeMsg(NetMsgId.char_skill_upgrade_failed_ack);
        }
        
        // Advance character
        int index = req.getIndex() - 1; // Lua indexes start at 1
        var change = character.upgradeSkill(index);
        
        if (change == null) {
            return session.encodeMsg(NetMsgId.char_skill_upgrade_failed_ack);
        }
        
        return session.encodeMsg(NetMsgId.char_skill_upgrade_succeed_ack, change.toProto());
    }

}
