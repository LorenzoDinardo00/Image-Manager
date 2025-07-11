package dao.impl;

import dao.UserDao;
import model.User;
import model.Role;
import util.DatabaseConnection;
import exception.AuthenticationException;

import java.sql.*;
import java.time.LocalDate;

public class UserDAOImpl implements UserDao {

    private boolean checkPassword(String storedPassword, String providedPassword) {
        return storedPassword.equals(providedPassword);
    }

    @Override
    public User findByUsernameAndPassword(String username, String password) throws SQLException, AuthenticationException {
        String sql = "SELECT username, name, surname, dateofbirth, cellphone, email, password, role "
                + "FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    if (checkPassword(storedPassword, password)) {
                        User user = new User();
                        user.setUsername(rs.getString("username"));
                        user.setName(rs.getString("name"));
                        user.setSurname(rs.getString("surname"));
                        Date sqlDateOfBirth = rs.getDate("dateofbirth");
                        if (sqlDateOfBirth != null) {
                            user.setDateOfBirth(sqlDateOfBirth.toLocalDate());
                        }
                        user.setCellphone(rs.getString("cellphone"));
                        user.setEmail(rs.getString("email"));
                        user.setPassword(rs.getString("password"));
                        String roleValue = rs.getString("role");
                        user.setRole(Role.fromDbValue(roleValue));
                        return user;
                    } else {
                        throw new AuthenticationException("Credenziali non valide.");
                    }
                } else {
                    throw new AuthenticationException("Credenziali non valide.");
                }
            }
        }
    }

    @Override
    public boolean registerUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, name, surname, dateofbirth, cellphone, email, password, role) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getName());
            ps.setString(3, user.getSurname());
            ps.setDate(4, Date.valueOf(user.getDateOfBirth()));
            ps.setString(5, user.getCellphone());
            ps.setString(6, user.getEmail());
            ps.setString(7, user.getPassword());
            ps.setString(8, user.getRole().getDbValue());

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                throw new SQLException("Errore nella registrazione: Username o Email già esistente.", e.getSQLState(), e);
            }
            throw e;
        }
    }
}