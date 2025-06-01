package service;


import model.User;
import exception.AuthenticationException;

import java.sql.SQLException;

public interface AuthService {

    User login(String username, String password) throws AuthenticationException, SQLException;
    boolean register(User user) throws SQLException, IllegalArgumentException;
    void logout(User user);
}