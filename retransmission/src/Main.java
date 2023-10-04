import ws.server.WebSocketServer;

public class Main{
    public static void main(String[] args) {
        new Thread(new WebSocketServer()).start();
    }
}