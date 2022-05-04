package srcClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;

public class clientLauncher extends Application {
    private Parent root;
    frmMain main;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/frmLogin.fxml"));
        root = loader.load();
        Scene scene = new Scene(root);
        sqlDriver sql = new sqlDriver();
        sql.createTable();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Basic Chat Client");
        stage.show();
        
    }

    @Override
    public void stop() throws IOException{
        List<String> clientSettings = new ArrayList<>(Files.readAllLines(Paths.get("./settings.ini"), StandardCharsets.UTF_8));
        if(clientSettings.size()<2){
            clientSettings.add("Username:"+frmLogin.staticTxtUsername.getText());
            Files.write(Paths.get("./settings.ini"), clientSettings, StandardOpenOption.WRITE);
        }
        else{
            clientSettings.set(1,"Username:"+frmLogin.staticTxtUsername.getText());
            Files.write(Paths.get("./settings.ini"), clientSettings, StandardCharsets.UTF_8);
        }

        if(clientSettings.size()<3){
            clientSettings.add("IP-Address:"+frmLogin.staticTxtIP.getText());
            Files.write(Paths.get("./settings.ini"), clientSettings, StandardOpenOption.WRITE);
        }
        else{
            clientSettings.set(2,"IP-Address:"+frmLogin.staticTxtIP.getText());
            Files.write(Paths.get("./settings.ini"), clientSettings, StandardCharsets.UTF_8);
        }
    }

    public static void main(String[] args) {
            launch();
        }
}