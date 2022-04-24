package srcServer;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;
 
public class serverLauncher extends Application {
    private Parent root;
    
    @Override
    public void start(Stage stage) throws IOException {
        //System.out.println(getClass().getResource("resources/frmServer.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/frmServer.fxml"));
        root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Basic Chat Server");
        stage.show();
        
    }

    @Override
    public void stop() throws IOException{
        //What happens after program close
    }
    
    public static void main(String[] args) {
            launch();
    }
}