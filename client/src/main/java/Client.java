import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectPropertyBase;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class Client extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Client cloud");
        primaryStage.setScene(new Scene(root, 900, 600));
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            ClientModel.getInstance().closeSocket();
            Platform.exit();
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
