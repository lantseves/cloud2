import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class CreateDirectoryController implements Initializable {
    public Button btnOk;
    public Button btnСancel;
    public TextField tfFileName;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnOk.setOnAction(event -> {
            if (tfFileName.getText().length() > 0) {
                ClientModel.getInstance().createDirectory(tfFileName.getText());
                ((Node)(event.getSource())).getScene().getWindow().hide();
            }
        });

        btnСancel.setOnAction(event -> {((Node)(event.getSource())).getScene().getWindow().hide();});
    }
}
