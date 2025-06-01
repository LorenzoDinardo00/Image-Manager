package model;

import java.time.LocalDateTime;

public class Post {
    private int postId;
    private String authorUsername;
    private byte[] imageData;
    private long imageSize;
    private String imageFormat;
    private String description;
    private int likesCount;
    private LocalDateTime createdAt;

    public Post() {
    }

    public Post(String authorUsername, byte[] imageData, long imageSize, String imageFormat, String description) {
        this.authorUsername = authorUsername;
        this.imageData = imageData;
        this.imageSize = imageSize;
        this.imageFormat = imageFormat;
        this.description = description;
        this.likesCount = 0;
    }

    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }
    public String getAuthorUsername() { return authorUsername; }
    public void setAuthorUsername(String authorUsername) { this.authorUsername = authorUsername; }
    public byte[] getImageData() { return imageData; }
    public void setImageData(byte[] imageData) { this.imageData = imageData; }
    public long getImageSize() { return imageSize; }
    public void setImageSize(long imageSize) { this.imageSize = imageSize; }
    public String getImageFormat() { return imageFormat; }
    public void setImageFormat(String imageFormat) { this.imageFormat = imageFormat; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getLikesCount() { return likesCount; }
    public void setLikesCount(int likesCount) { this.likesCount = likesCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Post{" +
                "postId=" + postId +
                ", authorUsername='" + authorUsername + '\'' +
                ", imageFormat='" + imageFormat + '\'' +
                ", imageSize=" + imageSize +
                ", imageData.length=" + (imageData != null ? imageData.length : 0) +
                ", description='" + description + '\'' +
                ", likesCount=" + likesCount +
                ", createdAt=" + createdAt +
                '}';
    }
}