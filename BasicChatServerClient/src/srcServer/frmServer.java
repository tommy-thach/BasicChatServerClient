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

    public static CheckBox staticChbxAutoStart;

    private ObservableList<sqlDriver> data;
    private serverConnection server;
    private int PORT;
    private boolean serverStarted=false;
    private serverThread serverThread;

    public class serverThread extends Thread {
        ServerSocket serverSocket;
        public void run() {
                try {
                    serverSocket = new ServerSocket(PORT);
                    server = new serverConnection(serverSocket);
                    System.out.println(this.getName() + " started.");
                    server.startServer();
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
        if(!txtPort.getText().isEmpty()){
            startServer();
        }
        else{
            txtConsole.appendText("[ERROR]: Please enter a port before starting the server.\n");
        }
    }

    @FXML
    void btnStopServerClicked(ActionEvent event) throws IOException {
        
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
        BufferedWriter bw = new BufferedWriter(new FileWriter("./server.ini"));
        if(chbxAutoStart.isSelected()==true){
            bw.write("Auto-Start:True");
            bw.newLine();
            bw.write("Port:"+txtPort.getText());
        }
        else{
            bw.write("Auto-Start:False");
            bw.newLine();
            bw.write("Port:"+txtPort.getText());
        }
        System.out.println("Closing buffer");
        bw.close();
    }

    @FXML
    void btnSendClicked(ActionEvent event) throws IOException {
        sendConsoleMessage();
    }

    @FXML
    void txtConsoleInputKeyPressed(KeyEvent event) throws Exception {
        if(event.getCode().equals(KeyCode.ENTER)){
            sendConsoleMessage();
        }
    }

    @FXML
    void btnBanClicked(ActionEvent event){
        setBan(1);
    }

    @FXML
    void btnUnbanClicked(ActionEvent event) {
        setBan(0);
    }

    @FXML
    void btnMakeAdminClicked(ActionEvent event) throws Exception {
        setAdmin(1);
    }

    @FXML
    void btnRevokeAdminClicked(ActionEvent event) throws Exception{
        setAdmin(0);
    }

    @FXML
    void btnRefreshDataClicked(ActionEvent event) throws Exception{
        loadData();
    }
    @FXML
    void btnDeleteAccClicked(ActionEvent event) throws Exception {
        data = tvUserList.getSelectionModel().getSelectedItems();
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);

        if(!data.isEmpty()){
            String selectedID = data.get(0).getID();
            String selectedUsername = data.get(0).getUsername();
            alert.setContentText("Are you sure you want to permanently delete " + selectedUsername +" account?");
            alert.showAndWait().ifPresent(type -> {
                if (type == ButtonType.YES) {
                    alert.setContentText("Are absolutely positive? This action cannot be undone.");
                    alert.showAndWait();
                    if(type == ButtonType.YES){
                        try{
                            Connection conn = sqlDriver.sqlConnect();
                            Statement statement = conn.createStatement();
                            statement.execute("DELETE from testtbl WHERE (`id` = '"+selectedID+"')");
                            loadData();
                            
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
        else{
            alert.setAlertType(AlertType.ERROR);
            alert.getButtonTypes().clear();
            alert.getButtonTypes().addAll(ButtonType.OK);
            alert.setContentText("Please select an entry from the table.");
            alert.showAndWait();
        }
    }

    public void startServer(){
        if(!serverStarted){
            serverStarted = true;
            PORT = Integer.parseInt(txtPort.getText());
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

    public void sendConsoleMessage() throws IOException{
        for (clientHandler users : serverConnection.handlerList) {
            users.getOut().write("[SERVER]: "+txtConsoleInput.getText());
            users.getOut().newLine();
            users.getOut().flush();
        }
        txtConsole.appendText("[SERVER]: "+txtConsoleInput.getText()+"\n");
        txtConsoleInput.clear();
    }

    public void setBan(int isBanned){
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
                        statement.execute("UPDATE `testdb`.`testtbl` SET `isBanned` = '"+isBanned+"' WHERE (`id` = '"+selectedID+"')");
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
        else{
            alert.setAlertType(AlertType.ERROR);
            alert.getButtonTypes().clear();
            alert.getButtonTypes().addAll(ButtonType.OK);
            alert.setContentText("Please select an entry from the table.");
            alert.showAndWait();
        }
    }

    public void setAdmin(int isAdmin){
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
                        statement.execute("UPDATE `testdb`.`testtbl` SET `isAdmin` = '"+isAdmin+"' WHERE (`id` = '"+selectedID+"')");
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
        else{
            alert.setAlertType(AlertType.ERROR);
            alert.getButtonTypes().clear();
            alert.getButtonTypes().addAll(ButtonType.OK);
            alert.setContentText("Please select an entry from the table.");
            alert.showAndWait();
        }
    }

    public void loadData() throws Exception{
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
        loadData();
        
        File serverFile = new File("server.ini");
        if(!serverFile.exists()){
            try (FileOutputStream fOutputStream = new FileOutputStream(serverFile,false)) {
            }
        }
        
        BufferedReader br = new BufferedReader(new FileReader("./server.ini"));
        String autoStartServer = br.readLine();
   
        if(autoStartServer != null){
            if(autoStartServer.substring(autoStartServer.lastIndexOf(":")+1).equals("True")){
                chbxAutoStart.setSelected(true);
                startServer();
            }

            String port=br.readLine();
            txtPort.setText(port.substring(port.lastIndexOf(":")+1));
            br.close();
        }
        
    }

}
