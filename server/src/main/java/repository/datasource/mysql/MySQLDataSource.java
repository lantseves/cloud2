package repository.datasource.mysql;

import repository.datasource.DataSource;
import repository.entity.File;
import repository.entity.User;
import java.sql.*;
import java.util.List;

//Класс для работы с БД
public class MySQLDataSource implements DataSource {

    private static final String url = "jdbc:mysql://localhost:3306/cloud?useUnicode=true&serverTimezone=UTC";
    private static final String user = "root";
    private static final String password = "root";

    private Connection conn ;

    private UserDataSource userDataSource;
    private FileDataSource fileDataSource ;

    public MySQLDataSource() {
        try {
            this.conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        userDataSource = new UserDataSource(conn) ;
        fileDataSource = new FileDataSource(conn, this) ;
    }

    @Override
    public List<User> getAllUserList() {
       return userDataSource.getAllUserList() ;
    }

    @Override
    public User getUserById(int id) {
        return userDataSource.getUserById(id);
    }

    @Override
    public User getUserByLogin(String login) {
        return userDataSource.getUserByLogin(login);
    }

    @Override
    public boolean getAuthorizationResult(String login, String password) {
        return userDataSource.getAuthorizationResult(login, password);
    }

    @Override
    public List<File> getAllFileList() {
        return fileDataSource.getAllFileList() ;
    }

    @Override
    public List<File> getFileListByParent(String parent) {
        return fileDataSource.getFileListByParent(parent);
    }

    @Override
    public List<File> getFileListByParent(File file) {
        return fileDataSource.getFileListByParent(file.getPath().toString());
    }

    @Override
    public List<File> getFileListByUser(int idUser) {
        return fileDataSource.getFileListByUser(idUser) ;
    }

    @Override
    public List<File> getFileListByUser(String login) {
        return fileDataSource.getFileListByUser(userDataSource.getUserByLogin(login).getId()) ;
    }

    @Override
    public List<File> getFileListOfOtherOwners(int idUser) {
        return fileDataSource.getFileListOfOtherOwners(idUser) ;
    }

    @Override
    public List<File> getFileListOfOtherOwners(String login) {
        return fileDataSource.getFileListOfOtherOwners(userDataSource.getUserByLogin(login).getId()) ;
    }

    @Override
    public List<File> getFullFileListAvailableUser(int idUser) {
        List<File> list = fileDataSource.getFileListByUser(idUser) ;
        list.addAll(fileDataSource.getFileListOfOtherOwners(idUser));
        return list ;
    }

    @Override
    public List<File> getFullFileListAvailableUser(String login) {
        return getFullFileListAvailableUser(userDataSource.getUserByLogin(login).getId());
    }

    @Override
    public void insertFile(File file) {
        fileDataSource.insertFile(file);
    }

    @Override
    public void updateFile(File file) {
        fileDataSource.updateFile(file);
    }

    @Override
    public void deleteFile(File file) {
        fileDataSource.deleteFile(file);
    }
}
