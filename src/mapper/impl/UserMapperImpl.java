package mapper.impl;
// src/mapper/impl/UserMapperImpl.java

import mapper.UserMapper;
import model.User;
import model.Role;
import java.sql.*;

public class UserMapperImpl implements UserMapper {

    @Override
    public User fromResultSet(ResultSet rs) throws SQLException {
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
        user.setRole(Role.fromDbValue(rs.getString("role")));

        return user;
    }

    @Override
    public void mapToStatement(PreparedStatement ps, User user) throws SQLException {
        ps.setString(1, user.getUsername());
        ps.setString(2, user.getName());
        ps.setString(3, user.getSurname());
        ps.setDate(4, Date.valueOf(user.getDateOfBirth()));
        ps.setString(5, user.getCellphone());
        ps.setString(6, user.getEmail());
        ps.setString(7, user.getPassword());
        ps.setString(8, user.getRole().getDbValue());
    }
}