package srcClient;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class frmLogin {
    @FXML
    private PasswordField txtPassword;

    @FXML 
    private TextField txtUsername;

    public static TextField staticTxtUsername;

    @FXML
    private TextField txtIP;

    public static TextField staticTxtIP;

    @FXML
    private CheckBox chbxAutoSignIn;

    @FXML
    private CheckBox chbxShowPassword;
    
    @FXML
    private CheckBox chbxRememberName;

    public static boolean staticChBxRememberNameSelected;

    private Stage stage;

    private static Socket socket=null;

    @FXML
    void btnRegister(ActionEvent event) throws IOException {
        Parent loader = FXMLLoader.load(getClass().getResource("resources/frmRegister.fxml"));
        Node node = (Node) event.getSource();
        stage = (Stage)node.getScene().getWindow();
        stage.setScene(new Scene(loader));
        stage.show();
    }

    @FXML
    void btnSignIn(ActionEvent event) throws Exception {
        signIn();
    }

    @FXML
    void txtIPKeyPressed(KeyEvent event) throws Exception {
        if(event.getCode().equals(KeyCode.ENTER)){
            signIn();
        }
    }

    @FXML
    void txtPasswordKeyPressed(KeyEvent event) throws Exception{
        if(event.getCode().equals(KeyCode.ENTER)){
            signIn();
        }
    }

    @FXML
    void txtUsernameKeyPressed(KeyEvent event) throws Exception {
        if(event.getCode().equals(KeyCode.ENTER)){
            signIn();
        }
    }
    @FXML
    void chbxAutoSignInClicked(ActionEvent event) throws Exception{
        //System.out.println(returnUsername());
    }

    @FXML
    void txtUsernameKeyTyped(KeyEvent event) throws IOException{

    }

    @FXML
    void chbxRememberNameClicked(MouseEvent event) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter("./settings.ini"));
        staticChBxRememberNameSelected = chbxRememberName.isSelected();
        if(chbxRememberName.isSelected()==true){
            bw.write("Remember-Username:true");
            bw.newLine();
            bw.write("Username:"+txtUsername.getText());
            bw.newLine();
            bw.write("IP-Address:"+txtIP.getText());
        }
        else{
            bw.write("Remember-Username:false");
            bw.newLine();
            bw.write("Username:"+txtUsername.getText());
            bw.newLine();
            bw.write("IP-Address:"+txtIP.getText());
        }
        System.out.println("Closing buffer");
        bw.close();
    }

    public void signIn() {
        Alert error = new Alert(AlertType.ERROR);
        error.setHeaderText(null);

        String username = txtUsername.getText();
        String password = txtPassword.getText();
        String IP = txtIP.getText().substring(0,txtIP.getText().lastIndexOf(":"));
        String PORT = txtIP.getText().substring(txtIP.getText().lastIndexOf(":")+1);
        try{
            socket = new Socket(IP,Integer.parseInt(PORT));
            System.out.println(socket);
            if(socket!=null){
                if(sqlDriver.isBanned(username)){
                    error.setContentText("You are banned from the server.");
                    error.showAndWait();
                }
                else{
                    if(txtIP.getText().isEmpty()){
                        error.setContentText("Please enter a valid IP and port address.");
                        error.showAndWait();
                    }
                    else{
                        if(sqlDriver.canLogin(username,password)){
                            Parent loader = FXMLLoader.load(getClass().getResource("resources/frmMain.fxml"));
                            Node node = (Node) txtUsername.getParent();
                            stage = (Stage)node.getScene().getWindow();
                            stage.setScene(new Scene(loader));
                            stage.show();
                        }
                        else{
                            error.setContentText("Invalid username or password.");
                            error.showAndWait();
                        }
                    }
                }
            }
        }
        catch(Exception e){
            error.setContentText("java.net.ConnectException: Connection refused.\nPlease check the IP/Port and try again.");
            error.showAndWait();
        }
    }

    public static Socket getSocket(){
        return socket;
    }

    @FXML
    public void initialize() throws IOException{
        File settingsFile = new File("settings.ini");
        String ipAddress=null;

        staticTxtUsername = txtUsername;
        staticTxtIP = txtIP;

        if(!settingsFile.exists()||settingsFile.length()==0){
            try (FileOutputStream fOutputStream = new FileOutputStream(settingsFile,false)) {
                BufferedWriter bw = new BufferedWriter(new FileWriter("./settings.ini"));
                bw.write("Remember-Username:");
                bw.newLine();
                bw.write("Username:");
                bw.newLine();
                bw.write("IP-Address:127.0.0.1:7777");
                bw.close();
            }
        }
        else{
            ipAddress = Files.readAllLines(Paths.get("./settings.ini")).get(2);
            txtIP.setText(ipAddress.substring(11));
        }

        BufferedReader br = new BufferedReader(new FileReader(settingsFile));
        String rememberNameIsChecked = br.readLine();
        staticChBxRememberNameSelected = Boolean.parseBoolean(rememberNameIsChecked.substring(rememberNameIsChecked.lastIndexOf(":")+1));
        if(rememberNameIsChecked != null){
            if(rememberNameIsChecked.substring(rememberNameIsChecked.lastIndexOf(":")+1).equals("true")){
                String username=br.readLine();
                chbxRememberName.setSelected(true);
                txtUsername.setText(username.substring(username.lastIndexOf(":")+1));
                br.close();
            }
        }
    }
}
