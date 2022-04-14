package srcServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class frmServer {

    @FXML
    private Button btnSend;

    @FXML
    private TextArea txtConsole;

    @FXML
    private TextField txtConsoleInput;

    @FXML
    void btnSendClicked(ActionEvent event) {

    }

    public void startServer(){
        int PORT = 5025;
        try {
            ServerSocket server = new ServerSocket(PORT);
            txtConsole.appendText("The TCP server is ready.");
            txtConsole.appendText("\nListening on port "+PORT+"..");
            while (true) {
                Socket connectionSocket = server.accept();
                DataInputStream in = new DataInputStream(connectionSocket.getInputStream());
                DataOutputStream out = new DataOutputStream(connectionSocket.getOutputStream());
                
                String requestString = in.readUTF();
                String responseString = requestString.toUpperCase();
                System.out.println("Message to be sent back to client: ");
                System.out.println(responseString);
                
                out.writeUTF(responseString);             
                out.flush();
                connectionSocket.close();
                in.close(); 
                out.close();
                server.close();
            }
        } catch(IOException e) { 
            e.printStackTrace();
        }
    }
    
    public class serverThread extends Thread {
        public void run(){
            startServer();
        }
    }

    @FXML
    public void initialize(){
        serverThread server = new serverThread();
        server.start();
    }
}
