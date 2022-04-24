package srcClient;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;

public class clientLauncher extends Application {
    private Parent root;
    frmMain main;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/frmLogin.fxml"));
        root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Basic Chat Client");
        stage.show();
        
    }

    @Override
    public void stop() throws IOException{
        //FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/frmLogin.fxml"));
        //loader.load();
        BufferedReader br = new BufferedReader(new FileReader("src/srcClient/user/login.txt"));
        String rememberUsernameIsChecked = br.readLine();
        System.out.println(rememberUsernameIsChecked.equals("True"));
        
        System.out.println("Stage is closing");
        
        if(rememberUsernameIsChecked.equals("True")){
            //frmLogin.rememberUsername();
            
            //System.out.println(frmLogin.getUsername());
        }
        br.close();
    }

    public static void main(String[] args) {
            launch();
        }
}