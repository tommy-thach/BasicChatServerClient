package srcServer;

import java.io.IOException;
import java.net.ServerSocket;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class frmServer {
    private static final int PORT = 5045;

    serverConnection serverStart;

    @FXML
    private Button btnSend;

    @FXML
    private TextArea txtConsole;

    public static TextArea staticTxtConsole;

    @FXML
    private TextField txtConsoleInput;

    public static TextField staticTxtConsoleInput;

    @FXML
    void btnSendClicked(ActionEvent event) {
        // serverMain.sendMessage(txtConsoleInput.getText());
    }

    public class serverThread extends Thread {
        public void run() {
            ServerSocket serverSocket;
            try {
                serverSocket = new ServerSocket(PORT);
                serverConnection server = new serverConnection(serverSocket);
                server.startServer();
                Thread.sleep(1);
            } catch (IOException | InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @FXML
    public void initialize() {
        staticTxtConsole = txtConsole;
        serverThread serverThread = new serverThread();
        serverThread.setDaemon(true);
        serverThread.start();
        txtConsole.appendText("Server started.\nListening on port "+PORT+"..\n");
    }

}
