package repository.datasource.mysql;

import repository.datasource.DataSource;
import repository.entity.File;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FileDataSource {

    private Connection conn ;
    private DataSource dataSource ;

    public FileDataSource(Connection conn, DataSource dataSource) {
        this.conn = conn;
        this.dataSource = dataSource ;
    }

    //Список всех файлов
    public List<File> getAllFileList() {
        List<File> result = new ArrayList<>() ;
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from files");
            while (rs.next()) {
                result.add(parseFile(rs)) ;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result ;
    }

    //Список файлов в выбраном каталоге
    public List<File> getFileListByParent(String path) {
        List<File> result = new ArrayList<>() ;
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("select * from files  where parent = ?");
            preparedStatement.setString(1, path);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                result.add(parseFile(rs)) ;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result ;
    }

    //Список файлов пользователя
    public List<File> getFileListByUser(int idUser) {
        List<File> result = new ArrayList<>() ;
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("select * from files  where owner = ?");
            preparedStatement.setInt(1, idUser);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                result.add(parseFile(rs)) ;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result ;
    }

    //Список файлов к которым открыли доступ другие пользователи, указанному пользователю
    public List<File> getFileListOfOtherOwners(int idUser) {
        List<File> result = new ArrayList<>() ;
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "select * from files f " +
                            "join files_users fu on f.id = fu.id_file where fu.id_user = ?");
            preparedStatement.setInt(1, idUser);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                result.add(parseFile(rs)) ;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result ;
    }

    //Поиск файла по id
    public File getFileByPId(int id) {
        File file = null;
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("select * from files  where id = ?");
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                file = parseFile(rs) ;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file ;
    }

    //Поиск файла по пути
    public File getFileByPath(Path path) {
        File file = null;
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("select * from files  where `path` = ?");
            preparedStatement.setString(1, path.toString());
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                file = parseFile(rs) ;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file ;
    }

    public void insertFile(File file) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "INSERT INTO files (path, owner, parent, file_name) VALUES (?, ?, ?, ?)");
            preparedStatement.setString(1, file.getPath().toString());
            preparedStatement.setInt(2, file.getOwner().getId());
            preparedStatement.setString(3, file.getParent().toString());
            preparedStatement.setString(4, file.getFilename());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateFile(File file) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "UPDATE files SET path = ?, owner = ?, parent = ?, file_name = ?  WHERE (id = ?)");
            preparedStatement.setString(1, file.getPath().toString());
            preparedStatement.setInt(2, file.getOwner().getId());
            preparedStatement.setString(3, file.getParent().toString());
            preparedStatement.setString(4, file.getFilename());
            preparedStatement.setInt(5, file.getId());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void deleteFile(File file) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM files WHERE (id = ?)");
            preparedStatement.setInt(3, file.getId());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private File parseFile(ResultSet rs) {
        File file = new File() ;
        try {
            file.setId(rs.getInt("id"));
            file.setPath(Paths.get(rs.getString("path")));
            file.setParent(Paths.get(rs.getString("parent")));
            file.setFilename(rs.getString("file_name"));
            file.setOwner(dataSource.getUserById(rs.getInt("owner")));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return file ;
    }
}
