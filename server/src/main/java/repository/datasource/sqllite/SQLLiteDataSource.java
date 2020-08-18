package repository.datasource.sqllite;

import repository.datasource.DataSource;
import repository.entity.File;
import repository.entity.User;

import java.nio.file.Path;
import java.sql.*;
import java.util.List;

//Класс для работы с БД
public class SQLLiteDataSource implements DataSource {

    private Connection conn ;

    private UserDataSource userDataSource;
    private FileDataSource fileDataSource ;

    public SQLLiteDataSource() {
        try {
            this.conn = DriverManager.getConnection("jdbc:sqlite:server/cloud.db");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        userDataSource = new UserDataSource(conn) ;
        fileDataSource = new FileDataSource(conn, this) ;
    }

    //Для создания файлика БД
    private void initSQLLite() {
        try {
            Connection c = DriverManager.getConnection("jdbc:sqlite:server/cloud.db");

            System.out.println("Opened database successfully");

            Statement stmt = c.createStatement();
            String sqlFiles = "CREATE TABLE files " +
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " path           TEXT    NOT NULL, " +
                    " owner            INT     NOT NULL, " +
                    " parent           TEXT    NOT NULL, " +
                    " file_name        CHAR(100) NOT NULL) ";
            stmt.executeUpdate(sqlFiles);
            stmt.close();

            Statement stmt1 = c.createStatement();
            String sqlUsers = "CREATE TABLE users " +
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " login        CHAR(45) NOT NULL, " +
                    " password        CHAR(45) NOT NULL, " +
                    " email        CHAR(45) NOT NULL, " +
                    " name        CHAR(45) NOT NULL, " +
                    " surname        CHAR(45) NOT NULL)";
            stmt1.executeUpdate(sqlUsers);
            stmt1.close();

            Statement stmt2 = c.createStatement();
            String sql3 = "CREATE TABLE files_users " +
                    " (id_user       INT NOT NULL, " +
                    " id_file        INT NOT NULL) " ;

            stmt2.executeUpdate(sql3);
            stmt2.close();

            Statement stmt3 = c.createStatement();
            String sqlInsert = "INSERT INTO users (login, password, email, name, surname) VALUES ('lantsev', '123', 'lantsev_es@mail.ru', 'Evgeny', 'Lantsev')" ;

            stmt3.executeUpdate(sqlInsert);
            stmt3.close();

            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
    public File getFileByPath(Path path) {
        return fileDataSource.getFileByPath(path) ;
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
