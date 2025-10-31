package emu.nebula.server.handlers;

import emu.nebula.net.NetHandler;
import emu.nebula.net.NetMsgId;
import emu.nebula.proto.GachaNewbieSpin.GachaNewbieSpinResp;
import emu.nebula.proto.GachaSpin.GachaSpinReq;
import emu.nebula.util.Utils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import emu.nebula.net.HandlerId;
import emu.nebula.data.GameData;
import emu.nebula.net.GameSession;

@HandlerId(NetMsgId.gacha_newbie_spin_req)
public class HandlerGachaNewbieSpinReq extends NetHandler {

    @Override
    public byte[] handle(GameSession session, byte[] message) throws Exception {
        @SuppressWarnings("unused")
        var req = GachaSpinReq.parseFrom(message);
        
        // Temp
        var list = new IntArrayList();
        
        for (var d : GameData.getCharacterDataTable()) {
            if (d.getGrade() == 1) {
                list.add(d.getId());
            }
        }
        
        // 
        var rsp = GachaNewbieSpinResp.newInstance();
        
        for (int i = 0; i < 10; i++) {
            int id = Utils.randomElement(list);
            rsp.addCards(id);
        }
        
        return session.encodeMsg(NetMsgId.gacha_newbie_spin_succeed_ack, rsp);
    }

}
