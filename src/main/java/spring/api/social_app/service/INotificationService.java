package spring.api.social_app.service;

import spring.api.social_app.dto.NotificationDTO;

import java.util.List;

public interface INotificationService {
    void sendFollowNotification(Long followerId, Long followedId);  // Thông báo khi có người theo dõi
    void sendLikeNotification(Long userId, Long postId);  // Thông báo khi có người thích bài viết
    void sendCommentNotification(Long userId, Long postId, Long commentId);  // Thông báo khi có người bình luận
    List<NotificationDTO> getNotifications(Long userId);  // Lấy thông báo của người dùng
}
