
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import main.java.message.FilePartMessage;
import netty.NettyClient;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    public Button send;
    public ListView<String> listView;
    public TextField text;
    private List<File> clientFileList;
    public static Socket socket;
    private DataInputStream is;
    private DataOutputStream os;
    private String clientPath ;
    private NettyClient nettyClient ;
    private ClientModel clientModel ;

    public void sendCommand(ActionEvent actionEvent) {
        FilePartMessage part = new FilePartMessage();
        part.setFileContent(text.getText().getBytes());
        nettyClient.writeMessage(part);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            nettyClient = new NettyClient();

            clientFileList = new ArrayList<>();
            clientPath = "./client/src/main/resources/";
            File dir = new File(clientPath);
            if (!dir.exists()) {
                throw new RuntimeException("directory resource not exists on client");
            }
            for (File file : Objects.requireNonNull(dir.listFiles())) {
                clientFileList.add(file);
                listView.getItems().add(file.getName());
            }

            listView.setOnMouseClicked(a -> {

            });
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File findFileByName(String fileName) {
        for (File file : clientFileList) {
            if (file.getName().equals(fileName)){
                return file;
            }
        }
        return null;
    }
}
