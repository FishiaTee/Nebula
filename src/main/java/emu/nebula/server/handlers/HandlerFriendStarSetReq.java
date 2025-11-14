package emu.nebula.server.handlers;

import emu.nebula.net.NetHandler;
import emu.nebula.net.NetMsgId;
import emu.nebula.proto.FriendStarSet.FriendStarSetReq;
import emu.nebula.net.HandlerId;
import emu.nebula.net.GameSession;

@HandlerId(NetMsgId.friend_star_set_req)
public class HandlerFriendStarSetReq extends NetHandler {

    @Override
    public byte[] handle(GameSession session, byte[] message) throws Exception {
        // Parse request
        var req = FriendStarSetReq.parseFrom(message);
        
        // Set star
        session.getPlayer().getFriendList().setStar(req.getUIds(), req.getStar());
        
        // Encode and send
        return session.encodeMsg(NetMsgId.friend_star_set_succeed_ack);
    }

}
