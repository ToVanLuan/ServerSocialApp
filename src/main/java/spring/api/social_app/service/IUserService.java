package spring.api.social_app.service;


import java.util.List;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import spring.api.social_app.dto.UserDTO;

public interface IUserService {
    UserDTO registerUser(UserDTO userDTO, String password, MultipartFile imgFile) throws Exception;
    String loginUser(String username, String password);
    void logoutUser(String token);
    UserDTO getUserProfile(Long userId);
    UserDTO updateUserProfile(Long userId, UserDTO userDTO, MultipartFile imgFile);
    boolean followUser(Long userId, Long targetUserId);
    boolean unfollowUser(Long userId, Long targetUserId);
    Set<UserDTO> getFollowers(Long userId);
    Set<UserDTO> getFollowing(Long userId);
    List<UserDTO> searchUsers(String keyword);
    boolean changePassword(Long userId, String oldPassword, String newPassword);
    boolean isEmailExists(String email);  // Kiểm tra email có tồn tại không
    String generateResetToken(String email);  // Tạo token reset mật khẩu
    boolean validateResetToken(String token);  // Kiểm tra token hợp lệ
    void resetPassword(String token, String newPassword);  // Cập nhật mật khẩu mới
    List<UserDTO> getAllUsers();
}


