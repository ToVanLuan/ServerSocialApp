package spring.api.social_app.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import spring.api.social_app.dto.NotificationDTO;
import spring.api.social_app.service.INotificationService;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationApi {

    @Autowired
    private INotificationService notificationService;

    // API để lấy danh sách thông báo của người dùng
    @GetMapping("/{userId}")
    public List<NotificationDTO> getNotifications(@PathVariable Long userId) {
        return notificationService.getNotifications(userId);
    }

    // API để gửi thông báo theo dõi
    @PostMapping("/follow")
    public String sendFollowNotification(@RequestParam Long followerId, @RequestParam Long followedId) {
        notificationService.sendFollowNotification(followerId, followedId);
        return "Follow notification sent successfully!";
    }

    // API để gửi thông báo thích bài viết
    @PostMapping("/like")
    public String sendLikeNotification(@RequestParam Long userId, @RequestParam Long postId) {
        notificationService.sendLikeNotification(userId, postId);
        return "Like notification sent successfully!";
    }

    // API để gửi thông báo bình luận bài viết
    @PostMapping("/comment")
    public String sendCommentNotification(@RequestParam Long userId, @RequestParam Long postId, @RequestParam Long commentId) {
        notificationService.sendCommentNotification(userId, postId, commentId);
        return "Comment notification sent successfully!";
    }
}
