package srcServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;


public class frmServer{
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
    private TextField txtPort;

    public static TextField staticTxtPort;

    @FXML
    private TableView<sqlDriver> tvUserList;

    @FXML
    private TableColumn<sqlDriver, String> columnID;

    @FXML
    private TableColumn<sqlDriver, String> columnUsername;

    @FXML
    private TableColumn<sqlDriver, String> columnPassword;

    @FXML
    private TableColumn<sqlDriver, String> columnEmail;

    @FXML
    private TableColumn<sqlDriver, String> columnDOB;
    
    @FXML
    private TableColumn<sqlDriver, String> columnGender;

    @FXML
    private TableColumn<sqlDriver, String> columnAdmin;

    @FXML
    private TableColumn<sqlDriver, String> columnBanned;

    @FXML
    private Button btnBan;

    @FXML
    private Button btnUnban;

    @FXML
    private Button btnDeleteAcc;

    @FXML
    private Button btnMakeAdmin;

    @FXML
    private Button btnRevokeAdmin;

    @FXML
    private Button btnRefreshData;

    @FXML
    private Button btnStartServer;

    @FXML
    private Button btnStopServer;

    @FXML
    private CheckBox chbxAutoStart;

    public static Boolean staticChBxAutoStartSelected;

    private ObservableList<sqlDriver> data;
    private serverConnection server;
    private int PORT;
    private boolean serverStarted=false;
    private serverThread serverThread;

    public class serverThread extends Thread { //Start the server connection on a new thread
        ServerSocket serverSocket;
        public void run() {
                try {
                    serverSocket = new ServerSocket(PORT); //Create server socket on specified port
                    server = new serverConnection(serverSocket);
                    System.out.println(this.getName() + " started.");
                    server.startServer(); //start the server
                } catch (IOException e) {
                    server.closeSockets();
                }
        }

        public void stopServer() throws IOException{
            System.out.println(this.getName() + " stopped.");
            server.stopServer();
        }
    }

    @FXML
    void btnStartServerClicked(ActionEvent event) {
        //Checks if port field is empty and if so, print error
        //Otherwise set the port and start the server on the specified port
        if(!txtPort.getText().isEmpty()){
            PORT = Integer.parseInt(txtPort.getText());
            startServer();
        }
        else{
            txtConsole.appendText("[ERROR]: Please enter a port before starting the server.\n");
        }
    }

    @FXML
    void btnStopServerClicked(ActionEvent event) throws IOException {
        //Checks to see if server is currently running and if so, display error
        //Otherwise, send signal to the server thread to stop the server
        serverThread.stopServer();
        if(serverStarted==true){
            serverStarted=false;
            txtConsole.appendText("[SERVER]: Server stopped.\n");
        }
        else{
            txtConsole.appendText("[ERROR]: The server is currently not running!\n");
        }
    }

    @FXML
    void chbxAutoStartClicked(MouseEvent event) throws IOException {
        //Update inputs to server.ini wether or not to start the server automatically on launch and the port specified
        BufferedWriter bw = new BufferedWriter(new FileWriter("./server.ini"));
        staticChBxAutoStartSelected=chbxAutoStart.isSelected();
        if(chbxAutoStart.isSelected()==true){
            bw.write("Auto-Start:true");
            bw.newLine();
            bw.write("Port:"+txtPort.getText());
        }
        else{
            bw.write("Auto-Start:false");
            bw.newLine();
            bw.write("Port:"+txtPort.getText());
        }
        System.out.println("Closing buffer");
        bw.close();
    }

    @FXML
    void btnSendClicked(ActionEvent event) throws IOException { //Send console message by clicking Send button
        sendConsoleMessage(); 
    }

    @FXML
    void txtConsoleInputKeyPressed(KeyEvent event) throws Exception { //Send console message by pressing enter on the keyboard
        if(event.getCode().equals(KeyCode.ENTER)){
            sendConsoleMessage();
        }
    }

    @FXML
    void btnBanClicked(ActionEvent event){ //Update the SQL of the specified user's isBanned field, setting it to 1 to ban them from the server
        setBan(1);
    }

    @FXML
    void btnUnbanClicked(ActionEvent event) { //Update the SQL of the specified user's isBanned field, setting it to 0 to unban them from the server
        setBan(0);
    }

    @FXML
    void btnMakeAdminClicked(ActionEvent event) throws Exception { //Update the SQL of the specified user's isAdmin field, setting it to 1 to make them an administrator
        setAdmin(1);
    }

    @FXML
    void btnRevokeAdminClicked(ActionEvent event) throws Exception{ //Update the SQL of the specified user's isAdmin field, setting it to 0 revoke their adminstrative access
        setAdmin(0);
    }

    @FXML
    void btnRefreshDataClicked(ActionEvent event) throws Exception{ //Refreshes the data table in the server client
        loadData();
    }

    @FXML
    void btnDeleteAccClicked(ActionEvent event) throws Exception {
        data = tvUserList.getSelectionModel().getSelectedItems(); //Gets the selected user from the table
        Alert alert = new Alert(AlertType.INFORMATION); 
        alert.setHeaderText(null);
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);

        //If the selection is valid, start deleting account
        if(!data.isEmpty()){
            String selectedID = data.get(0).getID(); //Grabs user's ID field from selection
            String selectedUsername = data.get(0).getUsername(); //Grabs user's Username field from selection
            alert.setContentText("Are you sure you want to permanently delete " + selectedUsername +" account?"); //Shows confirmation dialog
            alert.showAndWait().ifPresent(type -> {
                if (type == ButtonType.YES) {
                    alert.setContentText("Are absolutely positive? This action cannot be undone."); //If yes was clicked, show a second confirmation dialog
                    alert.showAndWait();
                    if(type == ButtonType.YES){
                        try{
                            Connection conn = sqlDriver.sqlConnect(); //Connect to SQL
                            Statement statement = conn.createStatement();
                            statement.execute("DELETE from testtbl WHERE (`id` = '"+selectedID+"')"); //If yes was clicked a second time, delete account
                            loadData(); //Refresh data table
                            
                            alert.getButtonTypes().clear();
                            alert.getButtonTypes().addAll(ButtonType.OK);
                            alert.setContentText(selectedUsername+"'s account has been permanently deleted.'");
                            alert.showAndWait();
                        }
                        catch(Exception e){
                        }
                    }
                } 
            });
        }
        else{ //If nothing is selected, send a warning
            alert.setAlertType(AlertType.ERROR);
            alert.getButtonTypes().clear();
            alert.getButtonTypes().addAll(ButtonType.OK);
            alert.setContentText("Please select an entry from the table.");
            alert.showAndWait();
        }
    }

    public void startServer(){
        //Checks if server is already started and if so, print error message
        //Otherwise start the server on the specified port
        if(!serverStarted){
            serverStarted = true;
            txtConsole.appendText("Starting server..\n");
            serverThread = new serverThread();
            serverThread.setDaemon(true);
            serverThread.start();
            System.out.println(serverThread.getName());
            txtConsole.appendText("Server started.\nListening on port "+PORT+"..\n");
        }
        else{
            txtConsole.appendText("[ERROR]: The server is currently running!\n");
        }
    }

    public void sendConsoleMessage() throws IOException{ //Sends a message from the server to all connected clients
        for (clientHandler users : serverConnection.handlerList) {
            users.getOut().write("[SERVER]: "+txtConsoleInput.getText());
            users.getOut().newLine();
            users.getOut().flush();
        }
        txtConsole.appendText("[SERVER]: "+txtConsoleInput.getText()+"\n");
        txtConsoleInput.clear();
    }

    public void setBan(int isBanned){ //function to ban the user: 0 = unbanned, 1 = banned
        data = tvUserList.getSelectionModel().getSelectedItems();
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);

        if(!data.isEmpty()){
            String selectedID = data.get(0).getID();
            String selectedUsername = data.get(0).getUsername();

            if(isBanned == 1){
                alert.setContentText("Are you sure you want to ban " + selectedUsername +"?");
            }
            else{
                alert.setContentText("Are you sure you want to unban " + selectedUsername +"?");
            }

            alert.showAndWait().ifPresent(type -> {
                if (type == ButtonType.YES) {
                    try{
                        Connection conn = sqlDriver.sqlConnect();
                        Statement statement = conn.createStatement();
                        System.out.println(selectedUsername);
                        statement.execute("UPDATE `testdb`.`testtbl` SET `isBanned` = '"+isBanned+"' WHERE (`id` = '"+selectedID+"')"); //send an update to the SQL database
                        loadData();
                        
                        alert.getButtonTypes().clear();
                        alert.getButtonTypes().addAll(ButtonType.OK);

                        if(isBanned==1){
                            alert.setContentText(selectedUsername+" has been banned.");
                            alert.showAndWait();
                        }
                        else{
                            alert.setContentText(selectedUsername+" has been unbanned.");
                            alert.showAndWait();
                        }
                    }
                    catch(Exception e){
                    }
                } 
            });
        }
        else{ //If nothing is selected, send a warning
            alert.setAlertType(AlertType.ERROR);
            alert.getButtonTypes().clear();
            alert.getButtonTypes().addAll(ButtonType.OK);
            alert.setContentText("Please select an entry from the table.");
            alert.showAndWait();
        }
    }

    public void setAdmin(int isAdmin){ //function to set the user's admin status: 0 = normal user, 1 = administrator
        data = tvUserList.getSelectionModel().getSelectedItems();
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);

        if(!data.isEmpty()){
            String selectedID = data.get(0).getID();
            String selectedUsername = data.get(0).getUsername();

            if(isAdmin == 1){
                alert.setContentText("Are you sure you want to promote " + selectedUsername +" to admin?");
            }
            else{
                alert.setContentText("Are you sure you want to revoke " + selectedUsername +"'s admin rights?");
            }

            alert.showAndWait().ifPresent(type -> {
                if (type == ButtonType.YES) {
                    try{
                        Connection conn = sqlDriver.sqlConnect();
                        Statement statement = conn.createStatement();
                        System.out.println(selectedUsername);
                        statement.execute("UPDATE `testdb`.`testtbl` SET `isAdmin` = '"+isAdmin+"' WHERE (`id` = '"+selectedID+"')"); //send an update to the SQL database
                        loadData();
                        
                        alert.getButtonTypes().clear();
                        alert.getButtonTypes().addAll(ButtonType.OK);

                        if(isAdmin==1){
                            alert.setContentText(selectedUsername+" has been promoted to admin.");
                            alert.showAndWait();
                        }
                        else{
                            alert.setContentText(selectedUsername+" has been demoted from admin.");
                            alert.showAndWait();
                        }
                    }
                    catch(Exception e){
                    }
                } 
            });
        }
        else{ //If nothing is selected, send a warning
            alert.setAlertType(AlertType.ERROR);
            alert.getButtonTypes().clear();
            alert.getButtonTypes().addAll(ButtonType.OK);
            alert.setContentText("Please select an entry from the table.");
            alert.showAndWait();
        }
    }

    public void loadData() throws Exception{ //Fills the table in the server with data from the SQL
        try{
            data = FXCollections.observableArrayList();
            Connection conn = sqlDriver.sqlConnect();
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM testtbl");
            while(rs.next()){
                data.add(new sqlDriver(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8)));
            }
        }
        catch(SQLException e){
            
        }
        columnID.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        columnPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
        columnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        columnDOB.setCellValueFactory(new PropertyValueFactory<>("dob"));
        columnGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        columnBanned.setCellValueFactory(new PropertyValueFactory<>("banned"));
        columnAdmin.setCellValueFactory(new PropertyValueFactory<>("admin"));
        tvUserList.setItems(data);
    }

    @FXML
    public void initialize() throws Exception {
        staticTxtConsole = txtConsole;
        staticTxtConsoleInput = txtConsoleInput;
        staticTxtPort=txtPort;
        
        loadData(); //Load the data on launch
        
        File serverFile = new File("server.ini"); //Checks if server config exist, if not, create one and fill it with default settings
        if(!serverFile.exists()||serverFile.length()==0){
            try (FileOutputStream fOutputStream = new FileOutputStream(serverFile,false)) {
                BufferedWriter bw = new BufferedWriter(new FileWriter(serverFile));
                bw.write("Auto-Start:false");
                bw.newLine();
                bw.write("Port:7777");
                bw.close();
            }
        }
        
        BufferedReader br = new BufferedReader(new FileReader(serverFile));

        //Fill in data from config into server on launch
        String autoStartServer = br.readLine();
        staticChBxAutoStartSelected = Boolean.parseBoolean(autoStartServer.substring(autoStartServer.lastIndexOf(":")+1));

        String port=br.readLine().substring(5);
        
        if(autoStartServer != null){ //If auto-start is set to true in the config, automatically start the server on launch
            if(autoStartServer.substring(autoStartServer.lastIndexOf(":")+1).equals("true")){
                chbxAutoStart.setSelected(true);
                PORT = Integer.parseInt(port);
                startServer();
            }

            txtPort.setText(port); 
            br.close();

            
        }
        
    }

}
