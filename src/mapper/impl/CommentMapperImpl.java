// src/mapper/impl/CommentMapperImpl.java
package mapper.impl;

import mapper.CommentMapper;
import model.Comment;
import java.sql.*;

public class CommentMapperImpl implements CommentMapper {

    @Override
    public Comment fromResultSet(ResultSet rs) throws SQLException {
        Comment comment = new Comment();
        comment.setCommentId(rs.getInt("comment_id"));
        comment.setPostId(rs.getInt("post_id"));
        comment.setCommenterUsername(rs.getString("commenter_username"));
        comment.setCommentText(rs.getString("comment_text"));

        Timestamp commentedAtTimestamp = rs.getTimestamp("commented_at");
        if (commentedAtTimestamp != null) {
            comment.setCommentedAt(commentedAtTimestamp.toLocalDateTime());
        }

        return comment;
    }

    @Override
    public void mapToInsertStatement(PreparedStatement ps, Comment comment) throws SQLException {
        ps.setInt(1, comment.getPostId());
        ps.setString(2, comment.getCommenterUsername());
        ps.setString(3, comment.getCommentText());
    }
}