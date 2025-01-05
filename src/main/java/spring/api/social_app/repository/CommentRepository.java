package spring.api.social_app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import spring.api.social_app.entity.Comment;
import spring.api.social_app.entity.Post;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findById(Long id);
    List<Comment> findByPostIdAndParentIsNull(Long postId);
    Long countByPost(Post post);
}
