package srcClient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class frmLogin {
    @FXML
    private PasswordField txtPassword;

    @FXML TextField txtUsername;
    
    @FXML
    private CheckBox chbxAutoSignIn;

    @FXML
    private CheckBox chbxShowPassword;
    
    @FXML
    private CheckBox chbxRememberName;

    
    

    private Stage stage;
    private Parent root;

    @FXML
    void btnRegister(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/frmRegister.fxml"));
        root = loader.load();
        Node node = (Node) event.getSource();
        stage = (Stage)node.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    void btnSignIn(ActionEvent event) throws Exception {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        if(sqlDriver.canLogin(username,password)){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/frmMain.fxml"));
            root = loader.load();
            Node node = (Node) event.getSource();
            stage = (Stage)node.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        }
        else{
            Alert error = new Alert(AlertType.ERROR);
            error.setHeaderText(null);
            error.setContentText("Invalid username or password.");
            error.showAndWait();
        }
        
    }

    @FXML
    void txtUsernameKeyTyped(KeyEvent event) throws IOException{
        if(chbxRememberName.isSelected() == true){
            rememberUsername();
        }
    }

    public void rememberUsername() throws IOException{
        BufferedWriter bw = new BufferedWriter(new FileWriter("src/srcClient/user/login.txt"));
        if(chbxRememberName.isSelected()==true){
            bw.write("True");
            bw.newLine();
        }
        else{
            bw.write("False");
            bw.newLine();
        }
        bw.write(txtUsername.getText());
        System.out.println("Closing buffer");
        bw.close();
    }

    @FXML
    public void initialize() throws IOException{
        BufferedReader br = new BufferedReader(new FileReader("src/srcClient/user/login.txt"));
        String rememberNameIsChecked = br.readLine();

        if(rememberNameIsChecked != null){
            if(rememberNameIsChecked.equals("True")){
                chbxRememberName.setSelected(true);
                txtUsername.setText(br.readLine());
                br.close();
            }
            else{
                System.out.println("");
            }
        }
        
    }
}
