package srcServer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;
 
public class serverLauncher extends Application {
    private Parent root;
    
    @Override
    public void start(Stage stage) throws IOException { //Loading the server form and open it
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
        //Save inputs into text file upon program closing so it could open with the same input next launch
        BufferedWriter bw = new BufferedWriter(new FileWriter("./server.ini"));
        bw.write("Auto-Start:"+frmServer.staticChBxAutoStartSelected);
        bw.newLine();
        bw.write("Port:"+frmServer.staticTxtPort.getText());
        bw.newLine();
        bw.close();
    }
    
    public static void main(String[] args) {
        launch();
    }
}