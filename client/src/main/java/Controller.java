import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.java.entity.FileResponse;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class Controller implements Initializable {

    //Клиент
    public TableView<FileView> tableViewClient;
    public TextField tfCurrentPath;
    public Button btnUpDirectory;
    public Button btnAddFile;
    public Button btnAddDirectory;
    public Button btnDeleteFile;
    public Button btnAuthUser;

    public Button btnUpload;
    public Button btnDownload;
    public FlowPane fpBtns;

    //Сервер
    public TableView<FileView> tableViewServer;
    public Button btnUpDirectoryServer;
    public TextField tfCurrentPathServer;
    public Button btnAddDirectoryServer;
    public Button btnDeleteFileServer;
    public HBox hbCurrentPath;
    public HBox hbButtonsServer;



    private ClientModel clientModel ;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //клиент
        initClientModel();
        initBtnAuthUser();
        initTableViewClient();
        initBtnUpDirectory();
        initBtnCreateFile();
        initBtnCreateDirectory();
        initBtnDeleteFile();
        tfCurrentPath.setText(".");

        initBtnUpload() ;
        initBtnDownload() ;
        // TODO public Button btnDownload;

        //Сервер
        initTableViewServer();
        initBtnUpDirectoryServer();
        initBtnAddDirectoryServer();
        initBtnDeleteFileServer();
    }

    // В данном методе вешается слушатель на ClientModel и описываются обработчики
    private void initClientModel() {
        clientModel = ClientModel.getInstance() ;
        clientModel.setGuiListener(new GUIListener() {
            @Override
            public void onChangeCurrentPath(Path currentPath) {
                Platform.runLater(() -> {
                    tfCurrentPath.setText(currentPath.toString());
                });
            }

            @Override
            public void onChangeCurrentPathServer(Path currentPath) {
                Platform.runLater(() -> {
                    tfCurrentPathServer.setText(currentPath.toString());
                });
            }

            @Override
            public void OnAuthresultOk() {
                Platform.runLater(() -> {
                    btnAuthUser.setDisable(true);
                    tableViewServer.setDisable(false);
                    hbCurrentPath.setDisable(false);
                    hbButtonsServer.setDisable(false);
                    fpBtns.setDisable(false);
                    clientModel.gelFileListServer("");
                });
            }

            @Override
            public void onChangeFileListCurrentPath(Path currentPath) {
                tableViewClient.setItems(getFileList(currentPath));
            }

            @Override
            public void onChangeFileListCurrentPathServer(List<FileResponse> list) {
                tableViewServer.setItems(getFileListServer(list));
            }
        });
    }

    private void initBtnAuthUser() {
        btnAuthUser.setOnAction(event -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("auth.fxml"));
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Окно авторизации");
                dialogStage.setScene(new Scene(root));
                dialogStage.setResizable(false);
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                dialogStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void initTableViewClient() {
        initTableViewColumn(tableViewClient);
        tableViewClient.setItems(getFileList(ClientModel.DEFAULT_PATH));
        tableViewClient.setOnMouseClicked( event -> {
            if( event.getClickCount() == 2 ) {
                FileView fileView = tableViewClient.getSelectionModel().getSelectedItem() ;
                if (fileView.isDirectory()) {
                    tableViewClient.setItems(getFileList(fileView.getPath()));
                } else {
                    //  TODO открыть файл
                }
            }
        });
    }

    private void initBtnUpDirectory() {
        btnUpDirectory.setOnAction(event -> {
            Path path = clientModel.upDirectoryClient() ;
            if (path != null) {
                tableViewClient.setItems(getFileList(path));
            }
        });
    }

    private void initBtnCreateFile() {
        btnAddFile.setOnAction(event -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("create_file.fxml"));
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Имя файла");
                dialogStage.setScene(new Scene(root));
                dialogStage.setResizable(false);
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                dialogStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void initBtnCreateDirectory() {
        btnAddDirectory.setOnAction(event -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("create_directory.fxml"));
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Имя файла");
                dialogStage.setScene(new Scene(root));
                dialogStage.setResizable(false);
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                dialogStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void initBtnDeleteFile() {
        btnDeleteFile.setOnAction(event -> {
            FileView file = tableViewClient.getSelectionModel().getSelectedItem() ;
            if (file != null)
            clientModel.deleteFile(file.getPath());
        });
    }


    private void initBtnUpload() {
        btnUpload.setOnAction(event -> {
            FileView file = tableViewClient.getSelectionModel().getSelectedItem() ;
            if (file != null)
                clientModel.writeFileToServer(file.getPath());
        });
    }

    private void initBtnDownload() {
        btnDownload.setOnAction(event -> {
            FileView file = tableViewServer.getSelectionModel().getSelectedItem() ;
            if (file != null)
                clientModel.downloadFile(file.getPath());
        });
    }


    private void initTableViewServer() {
        initTableViewColumn(tableViewServer);
        tableViewServer.setOnMouseClicked( event -> {
            if( event.getClickCount() == 2 ) {
                FileView fileView = tableViewServer.getSelectionModel().getSelectedItem() ;
                if (fileView.isDirectory()) {
                    clientModel.gelFileListServer(fileView.getPath().toString());
                }
            }
        });
    }

    private void initBtnUpDirectoryServer() {
        btnUpDirectoryServer.setOnAction(event -> {
            clientModel.upDirectoryServer();
        });
    }

    private void initBtnAddDirectoryServer() {
        btnAddDirectoryServer.setOnAction(event -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("create_directory_server.fxml"));
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Имя папки");
                dialogStage.setScene(new Scene(root));
                dialogStage.setResizable(false);
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                dialogStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void initBtnDeleteFileServer() {
        btnDeleteFileServer.setOnAction(event -> {
            FileView file = tableViewServer.getSelectionModel().getSelectedItem() ;
            if (file != null && !file.isDirectory()) clientModel.deleteFileServer(file.getPath());
        });
    }

    //Иниализирует столбцы в TableView
    private void initTableViewColumn(TableView<FileView> tableView) {
        TableColumn<FileView, ImageView> fileImageCol = new TableColumn<>("");
        fileImageCol.setCellValueFactory(new PropertyValueFactory<>("fileImage"));
        fileImageCol.setPrefWidth(25);
        fileImageCol.setMinWidth(25);
        fileImageCol.setEditable(false);
        fileImageCol.setSortType(TableColumn.SortType.DESCENDING);
        fileImageCol.setResizable(false);

        TableColumn<FileView, String> fileNameCol = new TableColumn<>("Имя файла");
        fileNameCol.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        fileNameCol.setPrefWidth(200);
        fileNameCol.setMinWidth(200);
        fileNameCol.setSortType(TableColumn.SortType.DESCENDING);

        TableColumn<FileView, String> fileDateModCol = new TableColumn<>("Дата Изменения");
        fileDateModCol.setCellValueFactory(new PropertyValueFactory<>("dateMod"));
        fileDateModCol.setPrefWidth(110);
        fileDateModCol.setMinWidth(110);
        fileDateModCol.setEditable(false);
        fileDateModCol.setSortType(TableColumn.SortType.DESCENDING);

        TableColumn<FileView, String> fileSizeCol = new TableColumn<>("Размер (Кб)");
        fileSizeCol.setCellValueFactory(new PropertyValueFactory<>("fileSize"));
        fileSizeCol.setPrefWidth(80);
        fileSizeCol.setMinWidth(80);
        fileSizeCol.setEditable(false);
        fileSizeCol.setSortable(false);
        tableView.getColumns().addAll(fileImageCol, fileNameCol, fileDateModCol, fileSizeCol);
    }

    //Преобразует List<Path> в ObservableList<FileView>
    private ObservableList<FileView> getFileList(Path path) {
        List<FileView> list = clientModel.getFileList(path)
                .stream()
                .map(FileView::new)
                .collect(Collectors.toList());

        return  FXCollections.observableArrayList(list);
    }

    //Преобразует List<FileResponse> в ObservableList<FileView>
    private ObservableList<FileView> getFileListServer(List<FileResponse> list) {
        List<FileView> result = list.stream()
                .map(i ->{
                    FileView file = new FileView();
                    file.setPath(Paths.get(i.getPath()));
                    file.setFileName(i.getFileName());
                    file.setDirectory(i.isDirectory());
                    if (!file.isDirectory()) {
                        file.setDateMod(i.getDateMod());
                        file.setFileSize(i.getFileSize());
                        file.setFileImage(new ImageView(new Image(
                                Paths.get("./client/src/main/resources/file.png")
                                        .toFile()
                                        .toURI()
                                        .toString())));

                    } else {
                        file.setFileImage(new ImageView(new Image(
                                Paths.get("./client/src/main/resources/directory.png")
                                        .toFile()
                                        .toURI()
                                        .toString())));
                    }
                    return file ;
                })
                .collect(Collectors.toList());
        return  FXCollections.observableArrayList(result);
    }
}
