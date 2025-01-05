package spring.api.social_app.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import spring.api.social_app.dto.UserDTO;
import spring.api.social_app.service.IUserService;

@RestController
@RequestMapping("/api/profile")
public class ProfileApi {

    @Autowired
    private IUserService userService;

    // Xem profile của người dùng (bao gồm profile của chính người dùng hoặc của người khác)
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserProfile(@PathVariable Long userId) {
        UserDTO userDTO = userService.getUserProfile(userId);
        if (userDTO != null) {
            return ResponseEntity.ok(userDTO);
        }
        return ResponseEntity.notFound().build();  // Nếu không tìm thấy user
    }

    // Chỉnh sửa profile của người dùng
    @PutMapping("/edit/{userId}")
    public ResponseEntity<UserDTO> updateUserProfile(@PathVariable Long userId,
                                                     @RequestParam String username,
                                                     @RequestParam String fullName,
                                                     @RequestParam String bio,
                                                     @RequestParam String email,
                                                     @RequestParam MultipartFile imgFile) {
        // Tạo UserDTO từ các tham số nhận được
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(username);
        userDTO.setFullName(fullName);
        userDTO.setBio(bio);
        userDTO.setEmail(email);
        UserDTO updatedUserDTO = userService.updateUserProfile(userId, userDTO,imgFile);
        if (updatedUserDTO != null) {
            return ResponseEntity.ok(updatedUserDTO);
        }
        return ResponseEntity.notFound().build();  // Nếu không tìm thấy user
    }
}
