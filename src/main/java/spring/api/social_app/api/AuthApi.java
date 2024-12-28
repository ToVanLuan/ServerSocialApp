package spring.api.social_app.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import spring.api.social_app.dto.UserDTO;
import spring.api.social_app.service.impl.EmailService;
import spring.api.social_app.service.IUserService;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthApi {

    @Autowired
    private IUserService userService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestParam String username,
                                            @RequestParam String fullName,
                                            @RequestParam String bio,
                                            @RequestParam String email,
                                            @RequestParam String password,
                                            @RequestParam MultipartFile imgFile) throws Exception {
        // Tạo UserDTO từ các tham số nhận được
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(username);
        userDTO.setFullName(fullName);
        userDTO.setBio(bio);
        userDTO.setEmail(email);

        // Đăng ký người dùng và nhận lại thông tin user
        UserDTO registeredUser = userService.registerUser(userDTO, password, imgFile);
        return ResponseEntity.ok(registeredUser);
    }


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
        // Đăng nhập và nhận lại token
        String token = userService.loginUser(username, password);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestParam String token) {
        // Đăng xuất và thông báo
        userService.logoutUser(token);
        return ResponseEntity.ok("Logged out successfully");
    }

    // change password
    @PostMapping("/{userId}/change-password")
    public String changePassword(@PathVariable Long userId,
                                 @RequestParam String oldPassword,
                                 @RequestParam String newPassword) {
        boolean success = userService.changePassword(userId, oldPassword, newPassword);
        if (success) {
            return "Password changed successfully";
        } else {
            return "Password change failed";
        }
    }

    // Quên mật khẩu
    @PostMapping("/forgot_password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        // Kiểm tra xem email có tồn tại không
        if (!userService.isEmailExists(email)) {
            return ResponseEntity.badRequest().body("Email không tồn tại!");
        }

        // Tạo token reset password (có thể là mã ngẫu nhiên hoặc mã hóa)
        String resetToken = userService.generateResetToken(email);

        // Tạo đường dẫn reset mật khẩu
        String resetLink = "localhost:8081/api/auth/reset_password?token=" + resetToken;

        // Gửi email reset mật khẩu
        emailService.sendPasswordResetEmail(email, resetLink);

        return ResponseEntity.ok("Đường dẫn reset mật khẩu đã được gửi đến email của bạn.");
    }

    @GetMapping("/reset_password")
    public ResponseEntity<String> resetPasswordGet(@RequestParam String token) {
        if (userService.validateResetToken(token)) {
            return ResponseEntity.ok("Token hợp lệ. Bạn có thể đặt lại mật khẩu.");
        } else {
            return ResponseEntity.badRequest().body("Token không hợp lệ hoặc đã hết hạn.");
        }
    }
    @GetMapping("/total")
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

}
