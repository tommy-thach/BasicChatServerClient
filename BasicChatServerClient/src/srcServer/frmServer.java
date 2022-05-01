package srcServer;

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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;


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
    
    private ObservableList<sqlDriver> data;

    @FXML
    void btnSendClicked(ActionEvent event) {
        // serverMain.sendMessage(txtConsoleInput.getText());
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
        serverThread serverThread = new serverThread();
        serverThread.setDaemon(true);
        serverThread.start();
        txtConsole.appendText("Server started.\nListening on port "+PORT+"..\n");
        loadData();

    }

}
