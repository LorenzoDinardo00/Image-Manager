// src/mapper/PostMapper.java
package mapper;

import model.Post;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface PostMapper extends Mapper<Post> {
    void mapToInsertStatement(PreparedStatement ps, Post post) throws SQLException;
}