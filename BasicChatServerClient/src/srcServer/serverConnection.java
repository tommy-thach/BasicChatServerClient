package srcServer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class serverConnection{

    private final ServerSocket serverSocket;
    Socket socket;
    public static CopyOnWriteArrayList<clientHandler> handlerList;

    public serverConnection(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {
        try {
            while (!serverSocket.isClosed()) {
                socket = serverSocket.accept();
                System.out.println("A user has successfully connected.");
                clientHandler handler = new clientHandler(socket);
                handlerList = handler.getHandlerList();
                Thread thread = new Thread(handler);
                thread.start();
            }
        } catch (Exception e) {
            closeSockets();
            Thread.currentThread().interrupt();
            return;
        }
    }

    public void closeSockets() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
