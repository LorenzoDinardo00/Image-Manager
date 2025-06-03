package dao.impl;

import dao.PostDao;
import mapper.PostMapper;
import mapper.impl.PostMapperImpl;
import model.Post;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostDAOImpl implements PostDao {

    private final PostMapper postMapper;

    // Constructor di default che crea il mapper internamente
    public PostDAOImpl() {
        this.postMapper = new PostMapperImpl();
    }

    // Constructor per dependency injection del mapper
    public PostDAOImpl(PostMapper postMapper) {
        this.postMapper = postMapper;
    }

    @Override
    public boolean createPost(Post post) throws SQLException {
        String sql = "INSERT INTO posts (author_username, image_data, image_size, image_format, description, likes_count) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            postMapper.mapToInsertStatement(ps, post);

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
        Post post = null;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    post = postMapper.fromResultSet(rs);
                }
            }
        }
        return post;
    }

    @Override
    public List<Post> getAllPosts() throws SQLException {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT post_id, author_username, image_data, image_size, image_format, description, likes_count, created_at FROM posts ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Post post = postMapper.fromResultSet(rs);
                posts.add(post);
            }
        }
        return posts;
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