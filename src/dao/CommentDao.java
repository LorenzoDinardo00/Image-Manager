package dao;

import model.Comment;

import java.sql.SQLException;
import java.util.List;

public interface CommentDao {

    boolean addComment(Comment comment) throws SQLException;


    List<Comment> getCommentsForPost(int postId) throws SQLException;
}