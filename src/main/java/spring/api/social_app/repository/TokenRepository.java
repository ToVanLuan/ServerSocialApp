package spring.api.social_app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import spring.api.social_app.entity.PasswordResetToken;

public interface TokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByEmail(String email);

    // Tìm token hết hạn
    boolean existsByToken(String token);
}
