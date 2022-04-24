package srcServer;
import java.net.ServerSocket;
import java.net.Socket;

public class serverConnection extends frmServer{

    private final ServerSocket serverSocket;

    public serverConnection(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("A user has successfully connected.");
                clientHandler handler = new clientHandler(socket);
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
