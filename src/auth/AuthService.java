package auth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthService {

    public boolean register(User user) {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            String sql = "INSERT INTO Users (USERNAME, Name, Surname, DateOfBirth, Cellphone, Email, Password) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getSurname());
            stmt.setDate(4, user.getDateOfBirth());
            stmt.setString(5, user.getCellphone());
            stmt.setString(6, user.getEmail());
            stmt.setString(7, user.getPassword());
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean login(String username, String password) {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            String sql = "SELECT * FROM Users WHERE USERNAME = ? AND Password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Se viene trovato un record, l'utente è autenticato
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}