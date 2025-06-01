package service;

import model.Post;
import model.Comment;
import model.User;

import java.sql.SQLException;
import java.util.List;

public interface PostService {
    boolean createPost(Post post, User currentUser) throws SQLException, IllegalArgumentException;
    Post getPostById(int postId) throws SQLException;
    List<Post> getAllPosts() throws SQLException;
    boolean addLikeToPost(int postId, User currentUser) throws SQLException, IllegalArgumentException;
    boolean addCommentToPost(Comment comment, User currentUser) throws SQLException, IllegalArgumentException;
    List<Comment> getCommentsForPost(int postId) throws SQLException, IllegalArgumentException;
}