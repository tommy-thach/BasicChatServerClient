package srcClient;

import java.io.*;

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

    private Stage stage;

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
        rememberUsername();
    }
    public void rememberUsername() throws IOException{
        BufferedWriter bw = new BufferedWriter(new FileWriter("./settings.ini"));
        if(chbxRememberName.isSelected()==true){
            bw.write("Remember-Username:True");
            bw.newLine();
            bw.write("Username:"+txtUsername.getText());
        }
        else{
            bw.write("Remember-Username:False");
            bw.newLine();
        }
        System.out.println("Closing buffer");
        bw.close();
    }

    public void signIn() throws Exception {
        Alert error = new Alert(AlertType.ERROR);
        error.setHeaderText(null);

        String username = txtUsername.getText();
        String password = txtPassword.getText();

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
    @FXML
    public void initialize() throws IOException{
        staticTxtUsername = txtUsername;
        staticTxtIP = txtIP;
        File settings = new File("settings.ini");
        if(!settings.exists()){
            FileOutputStream fOutputStream = new FileOutputStream(settings,false);
        }

        BufferedReader br = new BufferedReader(new FileReader("./settings.ini"));
        String rememberNameIsChecked = br.readLine();

        if(rememberNameIsChecked != null){
            if(rememberNameIsChecked.substring(rememberNameIsChecked.lastIndexOf(":")+1).equals("True")){
                String username=br.readLine();
                chbxRememberName.setSelected(true);
                txtUsername.setText(username.substring(username.lastIndexOf(":")+1));
                br.close();
            }
            else{
                System.out.println("");
            }
        }
    }
}
