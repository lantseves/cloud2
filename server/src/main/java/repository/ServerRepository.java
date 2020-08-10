package repository;

import main.java.message.AuthorizationMessage;
import main.java.message.FilePartMessage;
import repository.datasource.DataSource;
import repository.datasource.mysql.MySQLDataSource;
import repository.entity.File;
import repository.entity.User;
import repository.filesource.FileSource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ServerRepository {
    public final static String FILE_PATH = "./server/src/main/resources/users/";
    private final static Path ROOT_PATH = Paths.get(FILE_PATH);

    private DataSource dataSource ;
    private FileSource fileSource ;

    public ServerRepository() {
        this.dataSource = new MySQLDataSource();
        this.fileSource = new FileSource();
    }

    public boolean AuthorizationClient(AuthorizationMessage msg) {
        return dataSource.getAuthorizationResult(msg.getLogin(), msg.getPassword()) ;
    }

    public boolean writeFilePart(FilePartMessage msg, String login) {
        Path filePath = Paths.get(FILE_PATH + login + "/" + msg.getPath()) ;

        if (Files.exists(filePath)) {
            File fileData = dataSource.getFileByPath(filePath);
            if ( msg.getNumberPart() == 1) {
                if (fileSource.writeFilePart(filePath, msg.getFileContent(), false)) {
                    if (!login.equals(fileData.getOwner().getLogin())) {
                        fileData.setOwner(dataSource.getUserByLogin(login));
                        dataSource.updateFile(fileData);
                    }
                    return true ;
                } else {
                    return false ;
                }
            } else {
                return fileSource.writeFilePart(filePath, msg.getFileContent(), true) ;
            }
        } else {
            if(msg.getNumberPart() == 1) {
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
                return fileSource.writeFilePart(filePath, msg.getFileContent(), true) ;
            }
        }
    }

    public List<String> getFileListByParent(String path, String login) {
        List<String> result = new ArrayList<>();

        path = path.equals("./") ? "" : "/" + path ;
        Path parent = Paths.get(FILE_PATH + login + path) ;

        List<File> fileList = dataSource.getFileListByParent(parent.toString()) ;

        for(File file : fileList) {
            result.add(ROOT_PATH.relativize(file.getPath()).toString());
        }
        return result;
    }
}
