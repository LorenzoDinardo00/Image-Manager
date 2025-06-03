// src/mapper/UserMapper.java
package mapper;

import model.User;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface UserMapper extends Mapper<User> {
    void mapToStatement(PreparedStatement ps, User user) throws SQLException;
}