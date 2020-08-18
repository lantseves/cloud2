import main.java.entity.FileResponse;
import main.java.message.*;
import main.java.response.AuthorizationResponse;
import main.java.response.FileListResponse;
import netty.NettyClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// Отвечает за логику управления приложением
public class ClientModel {
    public static Path DEFAULT_PATH = Paths.get("./client/src/main/user_files") ;
    private static ClientModel instance;

    private AuthListener authListener ;
    private GUIListener guiListener ;

    private NettyClient nettyClient ;

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
                guiListener.onChangeFileListCurrentPathServer(list);
            } else if(msg instanceof FilePartMessage) {
                FilePartMessage fileMsg = (FilePartMessage) msg ;
                readFilePart(fileMsg) ;
            }
            System.out.println("Read server: " + msg);
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

    //Отправляет запрос на получение списка файлов в каталоге не сервере
    public void gelFileListServer(String path) {
        currentPathServer = Paths.get(path) ;
        guiListener.onChangeCurrentPathServer(currentPathServer);
        writeServer(new FileListMessage(path));
    }

    //Поднятся на уровень каталогов вверх на клиенте
    public Path upDirectoryClient() {
        if (!DEFAULT_PATH.equals(currentPath)) {
            return currentPath.getParent() ;
        }
        return null;
    }

    //Поднятся на уровень каталогов вверх на сервере
    public void upDirectoryServer() {
        if (!"".equals(currentPathServer.toString())) {
            Path parent = currentPathServer.getParent() ;

            //Не выходим выше корня папки на сервере
            if (parent != null) {
                currentPathServer = parent ;
                writeServer(new FileListMessage(currentPathServer.toString()));
                guiListener.onChangeCurrentPathServer(currentPathServer);
            } else {
                currentPathServer = Paths.get("") ;
                guiListener.onChangeCurrentPathServer(currentPathServer);
                writeServer(new FileListMessage(""));
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
        writeServer(new CreateDirectoryMessage(currentPathServer.toString() , directoryName));
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

    //Удаляет файл на сервере
    public void deleteFileServer(Path file) {
        writeServer(new DeleteFileMessage(file.toString()));
    }

    //Отправить файл на сервер
    public void writeFileToServer(Path path) {
        Path result ;
        if ("".equals(currentPathServer.toString())) {
            result = Paths.get(path.getFileName().toString());
        } else {
            result = Paths.get(currentPathServer + "/" + path.getFileName());
        }
        try(FileInputStream fis = new FileInputStream(path.toFile())) {
            int countParts = (int)Math.ceil(fis.available() / 1024f) ;
            for (int i = 1 ; fis.available() > 0 ; i++) {
                byte[] buffer = new byte[1024] ;
                FilePartMessage filePart = new FilePartMessage() ;
                filePart.setNumberPart(i);
                filePart.setCountParts(countParts);
                filePart.setPath(result.toString());
                filePart.setParent(currentPathServer.toString());
                fis.read(buffer);
                filePart.setFileContent(buffer);
                writeServer(filePart);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Получить файл с сервера
    public void downloadFile(Path filePath) {
        writeServer(new DownloadFileMessage(filePath.toString()));
    }

    //Отправляет запрос авторизации
    public void authorizationClient(String login, String password) {
        if (login.length() > 0 && password.length() > 0)
        writeServer(new AuthorizationMessage(login, password));
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

    //обрабатывает получение части файла с сервера
    private void readFilePart(FilePartMessage fileMsg) {
        Path path = Paths.get(fileMsg.getPath()) ;
        Path filePath = Paths.get(currentPath  + "/" + path.getFileName()) ;

        //Определяем это первый пакет или нет
        boolean appended = fileMsg.getNumberPart() != 1 ;
        try {
            if (Files.notExists(filePath)) {
                Files.createDirectories(filePath.getParent()) ;
            }
            if (!appended) {
                Files.write(filePath, fileMsg.getFileContent(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            } else {
                Files.write(filePath, fileMsg.getFileContent(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (fileMsg.getNumberPart() == fileMsg.getCountParts())
            guiListener.onChangeFileListCurrentPath(currentPath);
    }

    //Отправляет данные на сервер
    private void writeServer(Object obj) {
        System.out.println("Write server: " + obj);
        nettyClient.writeMessage(obj);
    }


}
