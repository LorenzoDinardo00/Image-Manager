// src/mapper/CommentMapper.java
package mapper;

import model.Comment;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface CommentMapper extends Mapper<Comment> {
    void mapToInsertStatement(PreparedStatement ps, Comment comment) throws SQLException;
}