package emu.nebula.net;

public abstract class NetHandler {

    public boolean requireSession() {
        return true;
    }
    
    public boolean requirePlayer() {
        return true;
    }
    
    // Handler

    public abstract byte[] handle(GameSession session, byte[] message) throws Exception;

}
