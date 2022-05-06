package srcClient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

    private static Socket socket = null;
    private BufferedReader in = null;
    private BufferedWriter out = null;
    private String ADDR, IP, PORT;

    @FXML
    public void exitApplication(ActionEvent event) {
        
    }



    @FXML
    void btnSendClicked(ActionEvent event) throws Exception{ //Send a message if the Send button is clicked
        sendMessage();
    }

    @FXML
    void txtChatInputKeyPressed(KeyEvent event) throws Exception { //Send a message if the enter key is pressed
        if(event.getCode().equals(KeyCode.ENTER)){
            sendMessage();
        }
    }
    
    @FXML
    void btnSignOutClicked(ActionEvent event) throws IOException { //Disconnect the user from the server
        disconnect();
    }

    @FXML
    public void initialize() throws IOException{ //Loads up all necessary information from config file
        staticTxtChatInput = txtChatInput;
        ADDR = frmLogin.staticTxtIP.getText();
        IP = ADDR.substring(0,ADDR.lastIndexOf(":"));
        PORT = ADDR.substring(ADDR.lastIndexOf(":")+1);
        lblWelcome.setText("Welcome " + sqlDriver.returnUsername());
        lblCurrDate.setText("Today is: " + getCurrDate());

        socket = frmLogin.getSocket();
        connect();
        System.out.println("CONNECT() RAN");
        System.out.println("SOCKET IS CONNECTED: " + socket.isConnected());

        
    }

    public void connect() throws UnknownHostException, IOException { //Attempt to connect the client to the server
        txtChat.appendText("Connecting..\n");
        txtChat.appendText("Connected to " + IP + " on port " + PORT + "!\n");
        System.out.println("Socket created");

        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        out.write(sqlDriver.returnUsername());
        out.newLine();
        out.flush();

        listenForMessage();
    }

    public void disconnect() throws IOException{ //Disconnects the user from the server, close their sockets, and return to login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/frmLogin.fxml"));
            Stage stage = (Stage) btnSignOut.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.show();

            out.write("[SERVER]: " + sqlDriver.returnUsername() + " has disconnected!");
            out.newLine();
            out.flush();

            closeSockets();
    }

    public void listenForMessage() { //Continuously listen for incoming messages from other users or the server
        Thread thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }

    public void sendMessage() throws Exception{ 
        try {
            if(!txtChatInput.getText().isEmpty()){//Sends a message if the input field is not empty
                if(txtChatInput.getText().startsWith("/")){ //If the input field starts with "/" then start searching for commands
                    checkForCommands();
                }
                else{
                    if(sqlDriver.isAdmin(sqlDriver.returnUsername())){ //If the user is an admin, display an admin prefix in chat before their messages
                        out.write("[ADMIN] " + sqlDriver.returnUsername() + ": " + txtChatInput.getText());
                    }
                    else{ //Otherwise display just their username normally
                        out.write(sqlDriver.returnUsername() + ": " + txtChatInput.getText());
                    }
                    out.newLine();
                    out.flush();
                }
                txtChatInput.clear(); //Clear the text field every time a message is sent
            }
        } catch (IOException e) {
            closeSockets();
            Thread.currentThread().interrupt();
        }
    }
    
    public void checkForCommands() throws Exception{ //Checks for which command is entered
        String input=txtChatInput.getText();
        String username;
        Connection conn = sqlDriver.sqlConnect();
        Statement statement = conn.createStatement();
        PreparedStatement getUsername;

        if(input.equals("/help")){ //Displays a list of available commands
            txtChat.appendText("-=[List of User Commands]=-\n");
            txtChat.appendText("[Command]\t\t[Usage]\n");
            txtChat.appendText("/help\t\t\tDisplays a list of all available commands\n");
            txtChat.appendText("/clear\t\t\tClears the chat screen\n");
            txtChat.appendText("/disconnect\t\tDisconnect yourself from the server\n");

            if(sqlDriver.isAdmin(sqlDriver.returnUsername())){ //If the user is an admin, display additional administrator commands
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
        else if(input.contains("/ban") && input.length()>5 && sqlDriver.isAdmin(sqlDriver.returnUsername())){
            username = input.substring(5);
            getUsername = conn.prepareStatement("SELECT username FROM testtbl WHERE username = '"+username+"'");
            ResultSet rs = getUsername.executeQuery();
            if(rs.next()){
                statement.execute("UPDATE `testdb`.`testtbl` SET `isBanned` = '1' WHERE (`username` = '"+username+"')");
                out.write("[SERVER]: "+rs.getString("Username")+" has been banned from the server.");
                out.newLine();
                out.flush();
            }
            else{
                txtChat.appendText("[ERROR]: The specified user '"+username+"' does not exist.\n");
            }
        }
        else if(input.contains("/unban") && input.length()>7 && sqlDriver.isAdmin(sqlDriver.returnUsername())){
            username = input.substring(7);
            getUsername = conn.prepareStatement("SELECT username FROM testtbl WHERE username = '"+username+"'");
            ResultSet rs = getUsername.executeQuery();
            if(rs.next()){
                statement.execute("UPDATE `testdb`.`testtbl` SET `isBanned` = '0' WHERE (`username` = '"+username+"')");
                out.write("[SERVER]: "+rs.getString("Username")+" has been unbanned from the server.");
                out.newLine();
                out.flush();
            }
            else{
                txtChat.appendText("[ERROR]: The specified user '"+username+"' does not exist.\n");
            }
        }
        else{
            txtChat.appendText("[Error] Invalid command! Enter /help for a list of available commands.\n");
        }
        //System.out.println(txtChatInput.getText());
    }

    public void closeSockets() { //Close all sockets
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

    public String getCurrDate() { //Gets the current date and display it in the client form
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
                closeSockets();
                Thread.currentThread().interrupt();
                break;
            }

        }
    }
}
