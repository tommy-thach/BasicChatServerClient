import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;
 
public class App extends Application {
    private Stage stage;
    private Parent root;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/frmLogin.fxml"));
        frmLogin frmLogin = loader.getController();

        root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        
    }

    @Override
    public void stop() throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/frmLogin.fxml"));
        loader.load();
        frmLogin frmLogin = loader.getController();
        BufferedReader br = new BufferedReader(new FileReader("src/user/login.txt"));
        String rememberUsernameIsChecked = br.readLine();
        System.out.println(rememberUsernameIsChecked.equals("True"));
        
        if(rememberUsernameIsChecked.equals("True")){
            //frmLogin.rememberUsername();
            
            //System.out.println(frmLogin.getUsername());
        }
        br.close();
    }

    public void rememberUsername() throws IOException{
        
        
    }
    public static void main(String[] args) {
            launch();
        }
}