package emu.nebula.server.handlers;

import emu.nebula.net.NetHandler;
import emu.nebula.net.NetMsgId;
import emu.nebula.proto.Ike.IKEReq;
import emu.nebula.proto.Ike.IKEResp;
import emu.nebula.net.HandlerId;
import emu.nebula.Nebula;
import emu.nebula.net.GameSession;

@HandlerId(NetMsgId.ike_req)
public class HandlerIkeReq extends NetHandler {

    @Override
    public boolean requireSession() {
        return false;
    }
    
    @Override
    public boolean requirePlayer() {
        return false;
    }
    
    @Override
    public byte[] handle(GameSession session, byte[] message) throws Exception {
        // Make sure we dont already have a session
        if (session != null) {
            return session.encodeMsg(NetMsgId.ike_failed_ack);
        }
        
        // Parse
        var req = IKEReq.parseFrom(message);
        
        // Create session
        session = new GameSession();
        session.setClientKey(req.getPubKey());
        session.generateServerKey();
        session.calculateKey();
        
        // Register session to game context
        Nebula.getGameContext().generateSessionToken(session);
        
        // Create response
        var rsp = IKEResp.newInstance()
                .setToken(session.getToken())
                .setCipher(session.getEncryptMethod())
                .setServerTs(Nebula.getCurrentTime())
                .setPubKey(session.getServerPublicKey());
        
        /*
        Nebula.getLogger().info("Client Public: " + Utils.base64Encode(session.getClientPublicKey()));
        Nebula.getLogger().info("Server Public: " + Utils.base64Encode(session.getServerPublicKey()));
        Nebula.getLogger().info("Server Private: " + Utils.base64Encode(session.getServerPrivateKey()));
        Nebula.getLogger().info("Key: " + Utils.base64Encode(session.getKey()));
        */
        
        // Encode and send to client
        return session.encodeMsg(NetMsgId.ike_succeed_ack, rsp);
    }

}
