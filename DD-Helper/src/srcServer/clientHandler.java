package srcServer;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class clientHandler extends frmServer implements Runnable {
    public static CopyOnWriteArrayList<clientHandler> handlerList = new CopyOnWriteArrayList<>();
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private String username;

    public clientHandler(Socket socket) {
        try {
            this.socket = socket;
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            username = in.readLine();

            handlerList.add(this);
            staticTxtConsole.appendText("[SERVER]: " + username + " has connected!\n");
        } catch (IOException e) {
            closeSockets();
        }
    }

    @Override
    public void run() {
        //Set<Thread> threads = Thread.getAllStackTraces().keySet();
        String message;
        while (socket!=null) {
            try {
                message = in.readLine();
                sendMessage(message);
                if(message!=null){
                    staticTxtConsole.appendText(message + "\n");
                }
                System.out.println(handlerList.size());
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

    public void sendMessage(String message) {
        for (clientHandler users : handlerList) {
            try {
                users.out.write(message);
                users.out.newLine();
                users.out.flush();
                
            } catch (Exception e) {
                closeSockets();
            }
        }
    }

    public void userDisconnect() {
        handlerList.remove(this);
    }

    public void closeSockets() {
        userDisconnect();
        try {
            if (socket != null) {
                socket.close();
            }
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
