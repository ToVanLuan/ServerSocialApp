package spring.api.social_app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import spring.api.social_app.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findById(Long id);
    long countByUserId(Long userId);
    List<Post> findByUserId(Long userId);
}
