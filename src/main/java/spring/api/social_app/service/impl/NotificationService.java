package spring.api.social_app.service.impl;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import spring.api.social_app.dto.NotificationDTO;
import spring.api.social_app.entity.Notification;
import spring.api.social_app.entity.Post;
import spring.api.social_app.entity.User;
import spring.api.social_app.repository.NotificationRepository;
import spring.api.social_app.repository.UserRepository;
import spring.api.social_app.service.INotificationService;

@Service
public class NotificationService implements INotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void sendFollowNotification(Long followerId, Long followedId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("Follower not found"));
        User followed = userRepository.findById(followedId)
                .orElseThrow(() -> new RuntimeException("Followed user not found"));

        String message = follower.getUsername() + " is now following you!";
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setTimestamp(LocalDateTime.now());
        notification.setUser(followed);  // Người nhận thông báo là người được theo dõi
        notification.setType("FOLLOW");

        notificationRepository.save(notification);
    }

    @Override
    public void sendLikeNotification(Long userId, Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Post post = user.getPosts().stream()
                .filter(p -> p.getId().equals(postId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Post not found"));

        String message = user.getUsername() + " liked your post: " + post.getContent();
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setTimestamp(LocalDateTime.now());
        notification.setUser(post.getUser());  // Người nhận thông báo là chủ bài viết
        notification.setType("LIKE");

        notificationRepository.save(notification);
    }

    @Override
    public void sendCommentNotification(Long userId, Long postId, Long commentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Post post = user.getPosts().stream()
                .filter(p -> p.getId().equals(postId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Post not found"));

        String message = user.getUsername() + " commented on your post: " + post.getContent();
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setTimestamp(LocalDateTime.now());
        notification.setUser(post.getUser());  // Người nhận thông báo là chủ bài viết
        notification.setType("COMMENT");

        notificationRepository.save(notification);
    }

    @Override
    public List<NotificationDTO> getNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserId(userId);

        return notifications.stream()
                .map(n -> new NotificationDTO(n.getId(), n.getMessage(), n.getTimestamp(), n.getType()))
                .collect(Collectors.toList());
    }
}
