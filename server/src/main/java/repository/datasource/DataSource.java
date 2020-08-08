package repository.datasource;

import repository.entity.File;
import repository.entity.User;

import java.util.List;

public interface DataSource {
    List<User> getAllUserList();

    User getUserById(int id);

    User getUserByLogin(String login);

    boolean getAuthorizationResult(String login, String password);

    List<File> getAllFileList();

    List<File> getFileListByParent(String parent) ;

    List<File> getFileListByParent(File file) ;

    List<File> getFileListByUser(int idUser);

    List<File> getFileListByUser(String login);

    List<File> getFileListOfOtherOwners(int idUser);

    List<File> getFileListOfOtherOwners(String login);

    List<File> getFullFileListAvailableUser(int idUser);

    List<File> getFullFileListAvailableUser(String login);

    void insertFile(File file);

    void updateFile(File file);

    void deleteFile(File file);
}
