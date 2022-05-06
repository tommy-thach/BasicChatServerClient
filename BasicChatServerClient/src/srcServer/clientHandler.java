package srcServer;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class clientHandler extends frmServer implements Runnable {
    public static CopyOnWriteArrayList<clientHandler> handlerList = new CopyOnWriteArrayList<>();
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private String username;

    public CopyOnWriteArrayList<clientHandler> getHandlerList(){
        return handlerList;
    }

    public BufferedWriter getOut() {
        return out;
    }

    public void setOut(BufferedWriter out) {
        this.out = out;
    }

    public clientHandler(Socket socket) { //Each connected user gets their own handler, grabs their username and adds them to list of handlers
        try {
            this.socket = socket;
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            setOut(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
            username = in.readLine();
            handlerList.add(this);
            
            sendMessage("[SERVER]: " + username + " has connected!");
            staticTxtConsole.appendText("[SERVER]: " + username + " has connected!\n");
        } catch (IOException e) {
            closeSockets();
        }
    }

    @Override
    public void run() {
        //Set<Thread> threads = Thread.getAllStackTraces().keySet();
        String message;
        while (socket!=null) { //While the socket is valid, connected users are able to send messages
            try {
                message = in.readLine();
                sendMessage(message);
                if(message!=null){
                    staticTxtConsole.appendText(message + "\n");
                }
                //System.out.println(handlerList.size());
                
                //System.out.printf("%-15s \t %-15s \t %-15s \t %s\n", "Name", "State", "Priority", "isDaemon");
                //for (Thread t : threads) {
                //    System.out.printf("%-15s \t %-15s \t %-15d \t %s\n", t.getName(), t.getState(), t.getPriority(),
                //            t.isDaemon());

                //}
            } catch (IOException e) {
                closeSockets();
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    public void sendMessage(String message) { //Echo message back to all connected users
        for (clientHandler users : handlerList) {
            try {
                users.getOut().write(message);
                users.getOut().newLine();
                users.getOut().flush();
                
            } catch (Exception e) {
                closeSockets();
            }
        }
    }

    public void userDisconnect() { //If a user disconnects, remove them from the handler list
        handlerList.remove(this);
    }

    public void closeSockets() { //Close all sockets
        userDisconnect();
        try {
            if (socket != null) {
                socket.close();
            }
            if (in != null) {
                in.close();
            }
            if (getOut() != null) {
                getOut().close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<sqlDriver> handlerList() {
        return null;
    }
}
