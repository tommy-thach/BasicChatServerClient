package srcClient;
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

    public void showLoginForm(ActionEvent event) throws Exception{ //Displays the login form
        Parent loader = FXMLLoader.load(getClass().getResource("resources/frmLogin.fxml"));
        Node node = (Node) event.getSource();
        stage = (Stage)node.getScene().getWindow();
        stage.setScene(new Scene(loader));
        stage.show();
    }

    @FXML
    void btnRegisterClicked(ActionEvent event) throws Exception { //Attempt to register the user once clicked
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        String email = txtEmail.getText();
        String emailExt = email.substring(email.lastIndexOf(".")+1);
        Set<String> emailExtSet = new HashSet<>(Arrays.asList("com","net","org","edu","gov")); //List of accepted email ending domains
        LocalDate localDate = dpBirthDate.getValue();
        String dob = "";
        String gender = "";
        Alert error = new Alert(AlertType.ERROR);
        error.setHeaderText(null);

        if(localDate != null){
            dob = dpBirthDate.getValue().toString();
        }
        if(cbGender.getValue() != null){
            gender = cbGender.getValue();
        }

        //Display error if specified field is empty or username/email is already registered
        if(username.isEmpty()){ 
            error.setContentText("Please enter a username.");
            error.showAndWait();
        }
        else if(password.isEmpty()){
            error.setContentText("Please enter a password.");
            error.showAndWait();
        }
        else if(!emailExtSet.contains(emailExt)){
            error.setContentText("Please enter a valid e-mail address.");
            error.showAndWait();
        }
        else if(dob.isEmpty()){
            error.setContentText("Please enter a date of birth.");
            error.showAndWait();
        }
        else if(gender.isEmpty()){
            error.setContentText("Please select a gender.");
            error.showAndWait();
        }
        else if(sqlDriver.containsDuplicateUsername(username) == true){
            error.setContentText("Your chosen username is already registered!");
            error.showAndWait();
        }
        else if(sqlDriver.containsDuplicateEMail(email) == true){
            error.setContentText("Your chosen e-mail is already registered!");
            error.showAndWait();
        }
        else{ //Continue to register the account once all conditions are met
            sqlDriver.addToSQL(username, password, email, dob, gender, 0, 0); //Add all the information to SQL database
            Alert success = new Alert(AlertType.INFORMATION);
            success.setHeaderText(null);
            success.setContentText("You have successfully registered!");
            success.showAndWait();

            showLoginForm(event); //Return to the login form

            //System.out.println("Username: " + username + "\nPassword: " + password +"\nE-Mail: " + email + "\nDOB: " + dob + "\nGender: " + gender);
            //System.out.println(emailExt);
        }
        
        
    }

    @FXML
    void btnCancelClicked(ActionEvent event) throws Exception { //Return back to the login form if cancel is clicked
        showLoginForm(event); 
    }

    public void initialize() throws IOException{
        cbGender.getItems().removeAll(cbGender.getItems());
        String[] genderList = {"Male","Female","Other"};

        cbGender.getItems().addAll(genderList);
    }
}
