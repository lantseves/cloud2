package repository;

import io.netty.channel.socket.SocketChannel;
import main.java.entity.FileResponse;
import main.java.message.AuthorizationMessage;
import main.java.message.FilePartMessage;
import repository.datasource.DataSource;
import repository.datasource.sqllite.SQLLiteDataSource;
import repository.entity.File;
import repository.filesource.FileSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

//Управляет логикой работы сервера
public class ServerRepository {
    public final static String STR_FILE_PATH = "./server/src/main/resources/users/";

    private DataSource dataSource ;
    private FileSource fileSource ;

    public ServerRepository() {
        this.dataSource = new SQLLiteDataSource();
        this.fileSource = new FileSource();
    }

    //Проверка логина и пароля клиента
    public boolean AuthorizationClient(AuthorizationMessage msg) {
        return dataSource.getAuthorizationResult(msg.getLogin(), msg.getPassword()) ;
    }

    //Список файлов для клиента
    public List<FileResponse> getFileListByParent(String pathStr, String login) {

        Path serverPath = Paths.get(STR_FILE_PATH + login);

        //Если запросили корень каталога
        pathStr = pathStr.equals("") ? "" : "/" + pathStr ;
        Path parent = Paths.get(serverPath + pathStr) ;

        //Преобразуем List<File> в List<FileResponse>
        return dataSource.getFileListByParent(parent.toString())
                .stream()
                .map(i -> {
                    try {
                        FileResponse file = new FileResponse();
                        file.setPath(serverPath.relativize(i.getPath()).toString());
                        file.setFileName(i.getFilename());
                        file.setDateMod(Files.getLastModifiedTime(i.getPath()).toString());
                        file.setFileSize(String.valueOf(Files.size(i.getPath()) / 1024)) ;
                        file.setDirectory(i.getPath().toFile().isDirectory());
                        return file;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null ;
                })
                .collect(Collectors.toList());
    }

    //Сохраняет часть файла на сервере
    public boolean writeFilePart(FilePartMessage msg, String login) {
        Path filePath = Paths.get(STR_FILE_PATH + login + "/" + msg.getPath()) ;

        if (Files.exists(filePath)) {
            //Получить описание файла из БД
            File fileData = dataSource.getFileByPath(filePath);
            //Если это первый пакет
            if ( msg.getNumberPart() == 1) {
                //Создать файл или заменить
                if (fileSource.writeFilePart(filePath, msg.getFileContent(), false)) {
                    //Если у файла сменился владелец, обновить
                    if (!login.equals(fileData.getOwner().getLogin())) {
                        fileData.setOwner(dataSource.getUserByLogin(login));
                        dataSource.updateFile(fileData);
                    }
                    return true ;
                } else {
                    return false ;
                }
            } else {
                //Дописать второй и следующие пакеты в файл
                return fileSource.writeFilePart(filePath, msg.getFileContent(), true) ;
            }
        } else {
            //Файл не существует и это первый пакет
            if(msg.getNumberPart() == 1) {
                //Создать его папки в БД, чтобы они учитывались при получение содержимого каталога
                createDirectoryInDB(filePath , login) ;
                //Создать файл или заменить
                if (fileSource.writeFilePart(filePath, msg.getFileContent(), false)) {
                    File file = new File();
                    file.setPath(filePath);
                    file.setParent(filePath.getParent());
                    file.setFilename(filePath.getFileName().toString());
                    file.setOwner(dataSource.getUserByLogin(login));
                    dataSource.insertFile(file);
                    return true ;
                } else {
                    return false ;
                }
            } else {
                //Дописать второй и следующие пакеты в файл
                return fileSource.writeFilePart(filePath, msg.getFileContent(), true) ;
            }
        }
    }

    //Загрузить файл с сервера
    public boolean downloadFile(String pathStr , SocketChannel socketChannel, String login) {
        //TODO проверить есть ли у пользователя права доступа к файлу
        Path file = Paths.get(STR_FILE_PATH + login + "/" + pathStr) ;
        return fileSource.readFilePart(file , socketChannel) ;
    }

    //Создает новый каталог
    public boolean createDirectory(String dirPathStr, String login) {
        Path dir = Paths.get(STR_FILE_PATH + login + "/" + dirPathStr) ;
        if (fileSource.createDirectory(dir)) {
            dataSource.insertFile(new File(
                    dir ,
                    dir.getParent(),
                    dir.getFileName().toString(),
                    dataSource.getUserByLogin(login)));
            return true ;
        }
        return false;
    }

    //Удалить файл
    public boolean deleteFile(String filePathStr, String login) {
        Path filePath = Paths.get(STR_FILE_PATH + login + "/" + filePathStr) ;
        if (fileSource.deleteFile(filePath)) {
            File file = dataSource.getFileByPath(filePath);
            dataSource.deleteFile(file);
            return true ;
        }
        return false ;
    }

    //Рекурсивно создает каталоги в БД, чтобы при запросе списка файлов, каталоги тоже были
    private void createDirectoryInDB(Path filePath, String login) {
        Path tempPath = filePath.getParent() ;
        for (int i = 0 ; i < filePath.getNameCount() - 1 ; i++) {
            if (dataSource.getFileByPath(tempPath) == null)
                dataSource.insertFile(new File(
                        tempPath ,
                        tempPath.getParent(),
                        tempPath.getFileName().toString(),
                        dataSource.getUserByLogin(login)));
            tempPath = tempPath.getParent();
        }
    }
}
