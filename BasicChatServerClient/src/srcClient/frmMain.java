package srcClient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class frmMain implements Runnable {
    private static final String IP_ADDR = "localhost";
    private static final int PORT = 5045;

    @FXML
    private DatePicker dpDatePicker;

    @FXML
    private Button btnSend;

    @FXML
    private Button btnSignOut;

    @FXML
    private Label lblCurrDate;

    @FXML
    private Label lblWelcome;

    @FXML
    private TextArea txtChat;

    @FXML
    private TextField txtChatInput;

    public static TextField staticTxtChatInput;

    Socket socket = null;
    BufferedReader in = null;
    BufferedWriter out = null;
    BufferedReader br = null;
    String message;
    Boolean userConnected = false;

    @FXML
    public void exitApplication(ActionEvent event) {
        
    }



    @FXML
    void btnSendClicked(ActionEvent event) throws Exception{
        sendMessage();
    }

    @FXML
    void txtChatInputKeyPressed(KeyEvent event) throws Exception {
        if(event.getCode().equals(KeyCode.ENTER)){
            sendMessage();
        }
    }
    
    @FXML
    void btnSignOutClicked(ActionEvent event) throws IOException {
        disconnect();
    }

    @FXML
    public void initialize() throws UnknownHostException, IOException {
        staticTxtChatInput = txtChatInput;
        lblWelcome.setText("Welcome " + sqlDriver.returnUsername());
        lblCurrDate.setText("Today is: " + getCurrDate());

        socket = new Socket(IP_ADDR,PORT);
        
        connect();
        System.out.println("CONNECT() RAN");
        System.out.println("SOCKET IS CONNECTED: " + socket.isConnected());
    }

    public void connect() throws UnknownHostException, IOException {
        txtChat.appendText("Connecting..\n");

        txtChat.appendText("Connected to " + IP_ADDR + " on port " + PORT + "!\n");
        System.out.println("Socket created");

        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        out.write(sqlDriver.returnUsername());
        out.newLine();
        out.flush();

        listenForMessage();
    }

    public void disconnect() throws IOException{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/frmLogin.fxml"));
            Stage stage = (Stage) btnSignOut.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.show();

            out.write("[SERVER]: " + sqlDriver.returnUsername() + " has disconnected!");
            out.newLine();
            out.flush();

            closeSockets();
    }

    public void listenForMessage() {
        Thread thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }

    public void sendMessage() throws Exception{
        try {
            if(!txtChatInput.getText().isEmpty()){
                if(txtChatInput.getText().startsWith("/")){
                    checkForCommands();
                }
                else{
                    if(sqlDriver.isAdmin(sqlDriver.returnUsername())){
                        out.write("[ADMIN] " + sqlDriver.returnUsername() + ": " + txtChatInput.getText());
                    }
                    else{
                        out.write(sqlDriver.returnUsername() + ": " + txtChatInput.getText());
                    }
                    out.newLine();
                    out.flush();
                }
                txtChatInput.clear();
            }
        } catch (IOException e) {
            closeSockets();
            Thread.currentThread().interrupt();
        }
    }
    
    public void checkForCommands() throws Exception{
        String input=txtChatInput.getText();

        if(input.equals("/help")){
            txtChat.appendText("-=[List of User Commands]=-\n");
            txtChat.appendText("[Command]\t\t[Usage]\n");
            txtChat.appendText("/help\t\t\tDisplays a list of all available commands\n");
            txtChat.appendText("/clear\t\t\tClears the chat screen\n");
            txtChat.appendText("/disconnect\t\tDisconnect yourself from the server\n");

            if(sqlDriver.isAdmin(sqlDriver.returnUsername())){
                txtChat.appendText("\n-=[List of Admin Commands]=-\n");
                txtChat.appendText("[Command]\t\t\t[Usage]\n");
                txtChat.appendText("/kick <username>\t\tKicks the specified user from the server.\n");
                txtChat.appendText("/ban <username>\t\tBans the specified user from the server.\n");
                txtChat.appendText("/unban <username>\tUnbans the specified user from the server.\n");
            }
        }
        else if(input.equals("/clear")){
            txtChat.clear();
        }
        else if(input.equals("/disconnect")){
            disconnect();
        }
        else if(input.contains("/ban") && sqlDriver.isAdmin(sqlDriver.returnUsername())){
            Connection conn = sqlDriver.sqlConnect();
            Statement statement = conn.createStatement();
            String username = input.substring(5,input.length());
            statement.execute("UPDATE `testdb`.`testtbl` SET `isBanned` = '1' WHERE (`username` = '"+username+"')");
            out.write("[SERVER]: "+username+" has been banned from the server.");
            out.newLine();
            out.flush();
        }
        else if(input.contains("/unban") && sqlDriver.isAdmin(sqlDriver.returnUsername())){
            Connection conn = sqlDriver.sqlConnect();
            Statement statement = conn.createStatement();
            String username = input.substring(7,input.length());
            statement.execute("UPDATE `testdb`.`testtbl` SET `isBanned` = '0' WHERE (`username` = '"+username+"')");
            txtChat.appendText("[SERVER]: " + username + " has been unbanned from the server.\n");
        }
        else{
            txtChat.appendText("[Error] Invalid command! Enter /help for a list of available commands.\n");
        }
        //System.out.println(txtChatInput.getText());
    }

    public void closeSockets() {
        try {
            if (socket != null) {
                socket.close();
                // System.out.println("Socket closed");
            }
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {

        }
    }

    public String getCurrDate() {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String currDate = dateFormat.format(date);

        return currDate;
    }

    @Override
    public void run() {
        String message;

        while (!socket.isClosed()) {
            try {
                message = in.readLine();
                txtChat.appendText(message + "\n");
                System.out.println(message);

            } catch (IOException e) {
                // Close everything gracefully.
                closeSockets();
                Thread.currentThread().interrupt();
                break;
            }

        }
    }
}
