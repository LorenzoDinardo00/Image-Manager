package service.impl;

import service.AuthService;
import dao.UserDao;
import model.User;
import exception.AuthenticationException;

import java.sql.SQLException;

public class AuthServiceImpl implements AuthService {

    private final UserDao userDAO;

    public AuthServiceImpl(UserDao userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public User login(String username, String password) throws AuthenticationException, SQLException {
        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Username e password non possono essere vuoti.");
        }

        return userDAO.findByUsernameAndPassword(username, password);
    }

    @Override
    public boolean register(User user) throws SQLException, IllegalArgumentException {
        try {
            user.validate();
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Errore di validazione: " + e.getMessage(), e);
        }


        try {
            return userDAO.registerUser(user);
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {

                throw new SQLException("Username o Email già esistente.", e.getSQLState(), e.getErrorCode(), e);
            }
            throw e;
        }
    }

    @Override
    public void logout(User user) {

        if (user != null) {
            System.out.println("Logout concettuale per l'utente: " + user.getUsername() + " nel Service Layer.");        }
    }
}