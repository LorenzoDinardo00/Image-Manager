package dao.impl;

import dao.CommentDao;
import model.Comment;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentDAOImpl implements CommentDao {

    @Override
    public boolean addComment(Comment comment) throws SQLException {
        String sql = "INSERT INTO comments (post_id, commenter_username, comment_text) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // --- LOGICA DEL MAPPER INTEGRATA QUI ---
            ps.setInt(1, comment.getPostId());
            ps.setString(2, comment.getCommenterUsername());
            ps.setString(3, comment.getCommentText());
            // --- FINE LOGICA INTEGRATA ---

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        comment.setCommentId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    @Override
    public List<Comment> getCommentsForPost(int postId) throws SQLException {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT comment_id, post_id, commenter_username, comment_text, commented_at FROM comments WHERE post_id = ? ORDER BY commented_at ASC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    comments.add(mapResultSetToComment(rs)); // Usa il metodo helper
                }
            }
        }
        return comments;
    }

    private Comment mapResultSetToComment(ResultSet rs) throws SQLException {
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
}