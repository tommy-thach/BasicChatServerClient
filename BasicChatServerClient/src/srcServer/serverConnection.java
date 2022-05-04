package srcServer;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class serverConnection{

    private Thread thread;
    private final ServerSocket serverSocket;
    private Socket socket;
    public static CopyOnWriteArrayList<clientHandler> handlerList;
    private static ArrayList<Socket> socketList = new ArrayList<Socket>();

    public serverConnection(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {
        try {
            while (!serverSocket.isClosed()) {
                socket = serverSocket.accept();
                socketList.add(socket);
                System.out.println(socketList);
                System.out.println("A user has successfully connected.");
                clientHandler handler = new clientHandler(socket);
                handlerList = handler.getHandlerList();
                thread = new Thread(handler);
                thread.start();
            }
        } catch (Exception e) {
            closeSockets();
            return;
        }
    }

    public void stopServer() throws IOException{
        serverSocket.close();

        for(Socket socket : socketList){
            if(socket!=null){
                socket.close();
            }
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
