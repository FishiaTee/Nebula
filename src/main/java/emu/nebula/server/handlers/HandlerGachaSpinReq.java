package emu.nebula.server.handlers;

import emu.nebula.net.NetHandler;
import emu.nebula.net.NetMsgId;
import emu.nebula.proto.GachaSpin.GachaCard;
import emu.nebula.proto.GachaSpin.GachaSpinReq;
import emu.nebula.proto.GachaSpin.GachaSpinResp;
import emu.nebula.proto.Public.ItemTpl;
import emu.nebula.util.Utils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import emu.nebula.net.HandlerId;
import emu.nebula.Nebula;
import emu.nebula.data.GameData;
import emu.nebula.net.GameSession;

@HandlerId(NetMsgId.gacha_spin_req)
public class HandlerGachaSpinReq extends NetHandler {

    @SuppressWarnings("unused")
    @Override
    public byte[] handle(GameSession session, byte[] message) throws Exception {
        var req = GachaSpinReq.parseFrom(message);
        
        // Temp
        var list = new IntArrayList();
        
        for (var def : GameData.getCharacterDataTable()) {
            if (def.getGrade() == 1 && def.isAvailable()) {
                list.add(def.getId());
            }
        }
        
        // Build response
        var rsp = GachaSpinResp.newInstance()
                .setTime(Nebula.getCurrentTime());
        
        rsp.getMutableChange();
        rsp.getMutableNextPackage();
        
        for (int i = 0; i < 10; i++) {
            int id = Utils.randomElement(list);
            
            var card = GachaCard.newInstance()
                    .setCard(ItemTpl.newInstance().setTid(id).setQty(1));
            
            rsp.addCards(card);
        }
        
        return session.encodeMsg(NetMsgId.gacha_spin_succeed_ack, rsp);
    }

}
