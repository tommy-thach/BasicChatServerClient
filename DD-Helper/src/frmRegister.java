import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class frmRegister {
    private Stage stage;
    private Parent root;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnRegister;

    @FXML
    private ChoiceBox<String> cbGender;

    @FXML
    private DatePicker dpBirthDate;

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private TextField txtUsername;

    @FXML
    void btnRegisterClicked(ActionEvent event) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        String email = txtEmail.getText();
        String emailExt = email.substring(email.lastIndexOf(".")+1);
        Set<String> emailExtSet = new HashSet<>(Arrays.asList("com","net","org","edu","gov"));
        LocalDate localDate = dpBirthDate.getValue();
        String dob = "";
        String gender = "";
        Alert error = new Alert(AlertType.ERROR);

        if(localDate != null){
            dob = dpBirthDate.getValue().toString();
        }
        if(cbGender.getValue() != null){
            gender = cbGender.getValue();
        }

        if(username.isEmpty()){
            error.setHeaderText("Please enter a username.");
            error.showAndWait();
        }
        else if(password.isEmpty()){
            error.setHeaderText("Please enter a password.");
            error.showAndWait();
        }
        else if(!emailExtSet.contains(emailExt)){
            error.setHeaderText("Please enter a valid e-mail address.");
            error.showAndWait();
        }
        else if(dob.isEmpty()){
            error.setHeaderText("Please enter a date of birth.");
            error.showAndWait();
        }
        else if(gender.isEmpty()){
            error.setHeaderText("Please select a gender.");
            error.showAndWait();
        }
        else{
            System.out.println("Username: " + username + "\nPassword: " + password +"\nE-Mail: " + email + "\nDOB: " + dob + "\nGender: " + gender);
            //System.out.println(emailExt);
        }
        
        
    }

    @FXML
    void btnCancelClicked(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/frmLogin.fxml"));
        root = loader.load();
        Node node = (Node) event.getSource();
        stage = (Stage)node.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void initialize() throws IOException{
        cbGender.getItems().removeAll(cbGender.getItems());
        String[] genderList = {"Male","Female","Other"};

        cbGender.getItems().addAll(genderList);
    }
}
