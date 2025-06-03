// src/mapper/impl/PostMapperImpl.java
package mapper.impl;

import mapper.PostMapper;
import model.Post;
import java.sql.*;

public class PostMapperImpl implements PostMapper {

    @Override
    public Post fromResultSet(ResultSet rs) throws SQLException {
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
    public void mapToInsertStatement(PreparedStatement ps, Post post) throws SQLException {
        ps.setString(1, post.getAuthorUsername());
        ps.setBytes(2, post.getImageData());
        ps.setLong(3, post.getImageSize());
        ps.setString(4, post.getImageFormat());
        ps.setString(5, post.getDescription());
        ps.setInt(6, 0); // likes_count iniziale
    }
}