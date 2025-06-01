package dao;

import model.Post;
import model.Comment; // Se i metodi per i commenti fossero qui

import java.sql.SQLException;
import java.util.List;

public interface PostDao {

    boolean createPost(Post post) throws SQLException;
    Post getPostById(int postId) throws SQLException;
    List<Post> getAllPosts() throws SQLException;
    boolean addLike(int postId) throws SQLException;

}