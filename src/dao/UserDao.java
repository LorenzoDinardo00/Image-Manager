package dao;

import model.User;
import model.Role;
import exception.AuthenticationException;

import java.sql.SQLException;
import java.time.LocalDate;

public interface UserDao {
    User findByUsernameAndPassword(String username, String password) throws SQLException, AuthenticationException;
    boolean registerUser(User user) throws SQLException;
}