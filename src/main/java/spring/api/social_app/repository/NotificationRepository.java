package spring.api.social_app.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import spring.api.social_app.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserId(Long userId);
}
