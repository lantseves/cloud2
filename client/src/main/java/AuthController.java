import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AuthController implements Initializable {
    public TextField tfLogin;
    public PasswordField pfPassword;
    public Label lMsg;
    public Button btnOk;
    public Button btnСancel;
    private ClientModel clientModel ;
    private Node node ;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.clientModel = ClientModel.getInstance() ;


        clientModel.setAuthListener((result, info) -> {
            if (result) {
                Platform.runLater(() -> {
                    node.getScene().getWindow().hide();
                });
            } else {
                Platform.runLater(() -> {
                lMsg.setText(info);
                lMsg.setVisible(true);
                });
            }
        });

        btnOk.setOnAction(event -> {
            node = (Node)event.getSource() ;
            String login = tfLogin.getText() ;
            String password = pfPassword.getText() ;
            if (login.length() <= 0 ) {
                lMsg.setText("Введите логин");
                lMsg.setVisible(true);
            } else if (password.length() <= 0 ) {
                lMsg.setText("Введите пароль");
                lMsg.setVisible(true);
            } else {
                clientModel.authorizationClient(login, password);
                lMsg.setText("Идет авторизация");
                lMsg.setVisible(true);
            }
        });

        btnСancel.setOnAction(event -> {((Node)(event.getSource())).getScene().getWindow().hide();});
    }

    public void set() {
        Platform.runLater(() -> {
            lMsg.setText("Идет авторизация");
            lMsg.setVisible(true);
        });
    }
}
