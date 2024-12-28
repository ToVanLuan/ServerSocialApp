package spring.api.social_app.dto;

import java.util.List;

public class PostDTO {
    private Long id; // ID của bài viết
    private String content; // Nội dung bài viết
    private List<String> imageUrls; // Danh sách URL của hình ảnh đính kèm
    private Long userId; // ID của người tạo bài viết
    private String username; // Tên người tạo bài viết
    private int likeCount; // Số lượt like
    private int commentCount; // Số lượng comment

    public PostDTO() {}
    public PostDTO(Long id, String content, List<String> imageUrls, int likeCount, int commentCount, String username) {
        this.id = id;
        this.content = content;
        this.imageUrls = imageUrls;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.username = username;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
}
