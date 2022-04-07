import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class frmLogin {
    @FXML
    private PasswordField txtPassword;

    @FXML 
    private TextField txtUsername;
    
    @FXML
    private CheckBox chbxAutoSignIn;

    @FXML
    private CheckBox chbxShowPassword;
    
    @FXML
    void btnRegister(ActionEvent event) {

    }
    private Stage stage;
    private Parent root;

    @FXML
    void btnSignIn(ActionEvent event) throws IOException {
        String username = txtUsername.getText();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/frmMain.fxml"));
        root = loader.load();
        frmMain frmMain = loader.getController();
        frmMain.getName(username);

        Node node = (Node) event.getSource();
        stage = (Stage)node.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }


}
