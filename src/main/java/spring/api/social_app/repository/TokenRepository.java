package spring.api.social_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.api.social_app.entity.PasswordResetToken;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByEmail(String email);

    // Tìm token hết hạn
    boolean existsByToken(String token);
}
