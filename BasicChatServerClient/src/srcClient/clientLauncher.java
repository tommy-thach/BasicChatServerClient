package srcClient;

import java.io.BufferedWriter;
import java.io.FileWriter;
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
        BufferedWriter bw = new BufferedWriter(new FileWriter("./settings.ini"));
        bw.write("Remember-Username:"+frmLogin.staticChBxRememberNameSelected);
        bw.newLine();
        bw.write("Username:"+frmLogin.staticTxtUsername.getText());
        bw.newLine();
        bw.write("IP-Address:"+frmLogin.staticTxtIP.getText());
        bw.newLine();
        bw.close();
    }

    public static void main(String[] args) {
            launch();
        }
}