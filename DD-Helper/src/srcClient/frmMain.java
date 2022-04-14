package srcClient;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class frmMain {
    @FXML
    private Label lblCurrDate;

    @FXML
    private Label lblWelcome;
    
    @FXML
    private DatePicker dpDatePicker;

    @FXML
    void btnSignOut(ActionEvent event) throws IOException {
        Node node = (Node) event.getSource();
        Parent root = FXMLLoader.load(getClass().getResource("resources/frmLogin.fxml"));
        Stage stage = (Stage)node.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void initialize(){
        lblCurrDate.setText("Today's Date: "+getCurrDate());
        dpDatePicker.setValue(LocalDate.now());
    }

    public void getName(String username){
        lblWelcome.setText("Welcome " + username + "!");
    }

    public String getCurrDate(){
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String currDate = dateFormat.format(date);

        return currDate;
    }
}
