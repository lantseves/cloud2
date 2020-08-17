package repository.datasource.sqllite;

import repository.entity.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Управляет данными о пользователях в БД и преобразует в объекты User
public class UserDataSource {

    private Connection conn ;

    public UserDataSource(Connection conn ) {
        this.conn = conn ;
    }

    //Список всех пользователей
    public List<User> getAllUserList() {
        String query = "select * from users";
        List<User> result = new ArrayList<>() ;
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                result.add(parseUser(rs)) ;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result ;
    }

    //Поиск пользователя по Id
    public User getUserById(int id) {
        User user = null;
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("select * from users  where id = ?");
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                user = parseUser(rs) ;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user ;
    }

    //Поиск пользователя по login
    public User getUserByLogin(String login) {
        User user = null;
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("select * from users  where login = ?");
            preparedStatement.setString(1, login);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                user = parseUser(rs) ;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user ;
    }

    // Проверка введенного логина и пароля пользователя
    public boolean getAuthorizationResult(String login, String password) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "select * from users  where login = ? and password = ?");
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                return true ;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false ;
    }

    //Преобразовывает строку результата запроса в объект User
    private User parseUser(ResultSet rs) {
        User user = new User();
        try {
            user.setId(rs.getInt("id"));
            user.setLogin(rs.getString("login"));
            user.setPassword(rs.getString("password"));
            user.setEmail(rs.getString("email"));
            user.setName(rs.getString("name"));
            user.setSurname(rs.getString("surname"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user ;
    }
}
