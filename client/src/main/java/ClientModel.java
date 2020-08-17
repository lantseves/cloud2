import main.java.entity.FileResponse;
import main.java.message.AuthorizationMessage;
import main.java.message.CreateDirectoryMessage;
import main.java.message.FileListMessage;
import main.java.response.AuthorizationResponse;
import main.java.response.FileListResponse;
import netty.NettyClient;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// Отвечает за логику управления приложением
public class ClientModel {
    public static Path DEFAULT_PATH = Paths.get("./client/src/main/user_files") ;
    private static ClientModel instance;

    private NettyClient nettyClient ;
    private AuthListener authListener ;
    private GUIListener guiListener ;
    private Path currentPath ;
    private Path currentPathServer ;

    public static ClientModel getInstance() {
        if (instance == null) {
            synchronized (ClientModel.class) {
                if (instance == null) {
                    instance = new ClientModel();
                } else {
                    return instance ;
                }
            }
        }
        return instance;
    }

    //Обрабатываем сообщения от сервера
    private ClientModel() {
        this.currentPath = Paths.get(DEFAULT_PATH.toString());
        this.nettyClient = new NettyClient(msg -> {
            if (msg instanceof AuthorizationResponse) {
                AuthorizationResponse authMsg = (AuthorizationResponse) msg ;
                if (authMsg.isResult()) {
                    authListener.readResultAuth(true , "Авторизация прошла успешно") ;
                    guiListener.OnAuthresultOk() ;
                } else {
                    authListener.readResultAuth(false, "Неверный логин или пароль");
                }
            } else if (msg instanceof FileListResponse) {
                FileListResponse fileListMsg = (FileListResponse) msg ;
                List<FileResponse> list = fileListMsg.getFileList();
                if (list.size() > 0) {
                    guiListener.onChangeFileListCurrentPathServer(list);
                }
            }
            System.out.println(msg);
        });
    }

    //Возвращает список файлов в каталоге на клиенте
    public List<Path> getFileList(Path path) {

        List<Path> result = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            stream.forEach(result::add);
        } catch (IOException | DirectoryIteratorException e) {
            e.printStackTrace();
        }

        this.currentPath = path ;
        //Передает в GUI только корень файлов, не показывает папки проекта
        guiListener.onChangeCurrentPath(Paths.get("./" + (DEFAULT_PATH.relativize(currentPath)).toString())) ;
        return result ;
    }

    public Path upDirectoryClient() {
        if (!DEFAULT_PATH.equals(currentPath)) {
            return currentPath.getParent() ;
        }
        return null;
    }

    public void upDirectoryServer() {
        if (!"".equals(currentPathServer.toString())) {
            Path parent = currentPathServer.getParent() ;
            if (parent != null) {
                nettyClient.writeMessage(new FileListMessage(parent.toString()));
            } else {
                nettyClient.writeMessage(new FileListMessage("./"));
                currentPathServer = Paths.get("") ;
                guiListener.onChangeCurrentPathServer(currentPathServer);
            }

        }
    }

    //Создает файл в текущей папке
    public void createFile(String fileName) {
        Path filePath = Paths.get(currentPath + "/" + fileName) ;
        try {
            Files.createFile(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        guiListener.onChangeFileListCurrentPath(currentPath) ;
    }

    //Создает папку в текущей папке
    public void createDirectory(String directoryName) {
        Path directoryPath = Paths.get(currentPath + "/" + directoryName) ;
        try {
            Files.createDirectories(directoryPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        guiListener.onChangeFileListCurrentPath(currentPath) ;
    }

    //Создает папку в текущем каталоге на сервере
    public void createDirectoryServer(String directoryName) {
        nettyClient.writeMessage(new CreateDirectoryMessage(currentPathServer.toString() , directoryName));
    }

    //Удаляет файл
    public void deleteFile(Path file) {
        try {
            if (Files.isDirectory(file)) {
                // Скопировал данный код, но не понял как он удаляет вложенные файлы
                Files.walk(file)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            } else {
                Files.delete(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.guiListener.onChangeFileListCurrentPath(currentPath);
    }

    //Отправляет запрос авторизации
    public void authorizationClient(String login, String password) {
        if (login.length() > 0 && password.length() > 0)
        nettyClient.writeMessage(new AuthorizationMessage(login, password));
    }

    public void gelFileListServer(String path) {
        currentPathServer = Paths.get(path) ;
        guiListener.onChangeCurrentPathServer(currentPathServer);
        nettyClient.writeMessage(new FileListMessage(path));
    }

    //Закрываем соединение с сервером
    public void closeSocket() {
        nettyClient.getChannel().close();
    }

    public void setAuthListener(AuthListener authListener) {
        this.authListener = authListener;
    }

    public void setGuiListener(GUIListener guiListener) {
        this.guiListener = guiListener;
    }
}
