package spring.api.social_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.api.social_app.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);
    List<User> findByUsernameContainingIgnoreCase(String username);
    List<User> findByFullNameContainingIgnoreCase(String fullName);
    Optional<User> findByEmail(String email);
}
