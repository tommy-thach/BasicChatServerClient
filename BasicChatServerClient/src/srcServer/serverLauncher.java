package srcServer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
        List<String> serverSettings = new ArrayList<>(Files.readAllLines(Paths.get("./server.ini"), StandardCharsets.UTF_8));

        for (int i = 0; i < serverSettings.size(); i++) {
            if (serverSettings.get(i).contains("Port:")) {
                serverSettings.set(i, "Port:"+frmServer.staticTxtPort.getText());
                break;
            }
        }

        Files.write(Paths.get("./server.ini"), serverSettings, StandardCharsets.UTF_8);
    }
    
    public static void main(String[] args) {
        launch();
    }
}