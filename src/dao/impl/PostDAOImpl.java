package dao.impl;

import dao.PostDao;
import model.Post;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostDAOImpl implements PostDao {

    @Override
    public boolean createPost(Post post) throws SQLException {
        String sql = "INSERT INTO posts (author_username, image_data, image_size, image_format, description, likes_count) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, post.getAuthorUsername());
            ps.setBytes(2, post.getImageData());
            ps.setLong(3, post.getImageSize());
            ps.setString(4, post.getImageFormat());
            ps.setString(5, post.getDescription());
            ps.setInt(6, 0);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        post.setPostId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    @Override
    public Post getPostById(int postId) throws SQLException {
        String sql = "SELECT post_id, author_username, image_data, image_size, image_format, description, likes_count, created_at FROM posts WHERE post_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPost(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Post> getAllPosts() throws SQLException {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT post_id, author_username, image_data, image_size, image_format, description, likes_count, created_at FROM posts ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                posts.add(mapResultSetToPost(rs));
            }
        }
        return posts;
    }

    private Post mapResultSetToPost(ResultSet rs) throws SQLException {
        Post post = new Post();
        post.setPostId(rs.getInt("post_id"));
        post.setAuthorUsername(rs.getString("author_username"));
        post.setImageData(rs.getBytes("image_data"));
        post.setImageSize(rs.getLong("image_size"));
        post.setImageFormat(rs.getString("image_format"));
        post.setDescription(rs.getString("description"));
        post.setLikesCount(rs.getInt("likes_count"));
        Timestamp createdAtTimestamp = rs.getTimestamp("created_at");
        if (createdAtTimestamp != null) {
            post.setCreatedAt(createdAtTimestamp.toLocalDateTime());
        }
        return post;
    }

    @Override
    public boolean addLike(int postId) throws SQLException {
        String sql = "UPDATE posts SET likes_count = likes_count + 1 WHERE post_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }
}