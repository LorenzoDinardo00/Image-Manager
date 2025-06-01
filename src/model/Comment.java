package model;

import java.time.LocalDateTime;

public class Comment {
    private int commentId;
    private int postId;
    private String commenterUsername;
    private String commentText;
    private LocalDateTime commentedAt;

    public Comment() {
    }

    public Comment(int postId, String commenterUsername, String commentText) {
        this.postId = postId;
        this.commenterUsername = commenterUsername;
        this.commentText = commentText;
    }

    public int getCommentId() { return commentId; }
    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }
    public int getPostId() {
        return postId;
    }
    public void setPostId(int postId) {
        this.postId = postId;
    }
    public String getCommenterUsername() {
        return commenterUsername;
    }
    public void setCommenterUsername(String commenterUsername) {
        this.commenterUsername = commenterUsername;
    }

    public String getCommentText() {
        return commentText;
    }
    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }
    public LocalDateTime getCommentedAt() {
        return commentedAt;
    }
    public void setCommentedAt(LocalDateTime commentedAt) {
        this.commentedAt = commentedAt;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "commentId=" + commentId +
                ", postId=" + postId +
                ", commenterUsername='" + commenterUsername + '\'' +
                ", commentText='" + commentText + '\'' +
                ", commentedAt=" + commentedAt +
                '}';
    }
}