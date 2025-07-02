package dao.impl;

import dao.UserDao;
import model.User;
import model.Role;
import model.mapper.UserMapper;
import util.DatabaseConnection;
import exception.AuthenticationException;

import java.sql.*;
import java.time.LocalDate;

public class UserDAOImpl implements UserDao {

    // NUOVO: Aggiungiamo il mapper
    private final UserMapper userMapper = new UserMapper();

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
                        // MODIFICATO: Ora usa il mapper invece del codice di mappatura manuale
                        return userMapper.fromResultSet(rs);
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

        String passwordToStore = user.getPassword();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // MODIFICATO: Ora usa il mapper per settare i parametri
            userMapper.setInsertParameters(ps, user);

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