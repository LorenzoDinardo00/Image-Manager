package dao.impl;

import dao.CommentDao;
import mapper.CommentMapper;
import mapper.impl.CommentMapperImpl;
import model.Comment;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentDAOImpl implements CommentDao {

    private final CommentMapper commentMapper;

    // Constructor di default che crea il mapper internamente
    public CommentDAOImpl() {
        this.commentMapper = new CommentMapperImpl();
    }

    // Constructor per dependency injection del mapper
    public CommentDAOImpl(CommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }

    @Override
    public boolean addComment(Comment comment) throws SQLException {
        String sql = "INSERT INTO comments (post_id, commenter_username, comment_text) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            commentMapper.mapToInsertStatement(ps, comment);

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
                    Comment comment = commentMapper.fromResultSet(rs);
                    comments.add(comment);
                }
            }
        }
        return comments;
    }
}