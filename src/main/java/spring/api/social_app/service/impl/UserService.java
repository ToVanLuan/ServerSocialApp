package spring.api.social_app.service.impl;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import jakarta.transaction.Transactional;
import spring.api.social_app.dto.UserDTO;
import spring.api.social_app.entity.PasswordResetToken;
import spring.api.social_app.entity.Post;
import spring.api.social_app.entity.User;
import spring.api.social_app.repository.TokenRepository;
import spring.api.social_app.repository.UserRepository;
import spring.api.social_app.service.IUserService;


@Service
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private Cloudinary cloudinary;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserDTO registerUser(UserDTO userDTO, String password, MultipartFile imgFile) {
        // Kiểm tra nếu user đã tồn tại không (dựa trên username)
        Optional<User> existingUser = userRepository.findByUsername(userDTO.getUsername());
        if (existingUser.isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        // Kiểm tra xem ảnh có hợp lệ không
        if (imgFile == null || imgFile.isEmpty()) {
            throw new RuntimeException("Avatar image is required.");
        }

        String urlImg = uploadAvatarToCloudinary(imgFile); // Lưu avatar và lấy URL

        // Tạo người dùng mới
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setFullName(userDTO.getFullName());
        user.setPassword(passwordEncoder.encode(password));  // Mã hóa mật khẩu
        user.setBio(userDTO.getBio());
        user.setEmail(userDTO.getEmail());
        user.setAvatar(urlImg); // Lưu URL của ảnh avatar

        // Lưu người dùng vào cơ sở dữ liệu
        User savedUser = userRepository.save(user);

        // Trả về UserDTO với thông tin người dùng đã lưu
        return new UserDTO(savedUser.getId(), savedUser.getUsername(), savedUser.getFullName(),
                savedUser.getBio(), savedUser.getAvatar(), savedUser.getEmail());
    }

    private String uploadAvatarToCloudinary(MultipartFile imgFile) {
        try {
            Map uploadResult = cloudinary.uploader().upload(imgFile.getBytes(), ObjectUtils.asMap(
                    "folder", "avatars" // Tạo thư mục riêng cho avatar

            ));	

            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload avatar to Cloudinary", e);
        }
    }

    @Override
    public String loginUser(String username, String password) {
        // Kiểm tra nếu người dùng tồn tại
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
        	Long userId = user.get().getId();
            // Trả về token giả lập (thực tế sử dụng JWT)
            return "dummy-token-for-" + userId;

        }
        throw new RuntimeException("Invalid username or password");
    }

    @Override
    public void logoutUser(String token) {
        // Quá trình hủy token (nếu sử dụng JWT, bạn có thể quản lý token đã hủy ở đây)
        System.out.println("Token invalidated: " + token);
    }

	    @Override
	    public UserDTO getUserProfile(Long userId) {
	        Optional<User> userOptional = userRepository.findById(userId);
	        if (userOptional.isPresent()) {
	            User user = userOptional.get();
	            Set<String> posts = new HashSet<>();
	            Set<String> followers = new HashSet<>();
	            Set<String> following = new HashSet<>();
	
	            // Chuyển đổi thông tin về bài viết, người theo dõi và người theo dõi
	            for (Post post : user.getPosts()) {
	                posts.add(post.getId().toString());
	            }
	            for (User follower : user.getFollowers()) {
	                followers.add(follower.getUsername());
	            }
	            for (User followee : user.getFollowing()) {
	                following.add(followee.getUsername());
	            }
	
	            return new UserDTO(user.getId(), user.getUsername(), user.getFullName(), user.getBio(), user.getAvatar(), posts, followers, following,user.getEmail());
	        }
	        throw new RuntimeException("User not found");
	    }
	

    @Override
    public UserDTO updateUserProfile(Long userId, UserDTO userDTO, MultipartFile imgFile) {
        Optional<User> userOptional = userRepository.findById(userId);

        // Kiểm tra xem ảnh có hợp lệ không
        if (imgFile == null || imgFile.isEmpty()) {
            throw new RuntimeException("Avatar image is required.");
        }

        String urlImg = uploadAvatarToCloudinary(imgFile); // Lưu avatar và lấy URL
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setUsername(userDTO.getUsername());
            user.setFullName(userDTO.getFullName());
            user.setBio(userDTO.getBio());
            user.setAvatar(urlImg);
            user.setEmail(userDTO.getEmail());
            Set<String> followers = new HashSet<>();
            Set<String> following = new HashSet<>();
            Set<String> posts = new HashSet<>();

            // Lấy danh sách bài viết của người dùng
            for (Post post : user.getPosts()) {
                posts.add(post.getId().toString()); // Hoặc thêm bất kỳ thông tin nào bạn muốn hiển thị từ bài viết
            }

            // Lấy danh sách followers
            for (User follower : user.getFollowers()) {
                followers.add(follower.getUsername());
            }

            // Lấy danh sách following
            for (User followee : user.getFollowing()) {
                following.add(followee.getUsername());
            }


            User updatedUser = userRepository.save(user);
            return new UserDTO(updatedUser.getId(), updatedUser.getUsername(),
                    updatedUser.getFullName(), updatedUser.getBio(),
                    updatedUser.getAvatar(),posts,followers,following,updatedUser.getEmail());
        }
        throw new RuntimeException("User not found");
    }

    @Override
    @Transactional
    public boolean followUser(Long userId, Long targetUserId) {
        // Kiểm tra nếu người dùng và người được theo dõi có tồn tại
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<User> targetUserOpt = userRepository.findById(targetUserId);

        if (userOpt.isPresent() && targetUserOpt.isPresent()) {
            User user = userOpt.get();
            User targetUser = targetUserOpt.get();

            // Kiểm tra xem người dùng có phải là chính mình không
            if (userId.equals(targetUserId)) {
                throw new RuntimeException("You cannot follow yourself");
            }

            // Kiểm tra xem người dùng đã theo dõi targetUser chưa
            if (!user.getFollowing().contains(targetUser)) {
                user.getFollowing().add(targetUser);  // Thêm targetUser vào danh sách following
                targetUser.getFollowers().add(user);  // Thêm user vào danh sách followers của targetUser

                userRepository.save(user);  // Lưu lại thông tin người dùng
                userRepository.save(targetUser);  // Lưu lại thông tin targetUser

                return true;  // Thành công
            } else {
                throw new RuntimeException("You are already following this user");
            }
        } else {
            throw new RuntimeException("User or target user not found");
        }
    }

    @Override
    @Transactional
    public boolean unfollowUser(Long userId, Long targetUserId) {
        // Kiểm tra nếu người dùng và người được bỏ theo dõi có tồn tại
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<User> targetUserOpt = userRepository.findById(targetUserId);

        if (userOpt.isPresent() && targetUserOpt.isPresent()) {
            User user = userOpt.get();
            User targetUser = targetUserOpt.get();

            // Kiểm tra xem người dùng có phải là chính mình không
            if (userId.equals(targetUserId)) {
                throw new RuntimeException("You cannot unfollow yourself");
            }

            // Kiểm tra nếu người dùng đang theo dõi targetUser
            if (user.getFollowing().contains(targetUser)) {
                user.getFollowing().remove(targetUser);  // Bỏ targetUser khỏi danh sách following
                targetUser.getFollowers().remove(user);  // Bỏ user khỏi danh sách followers của targetUser

                userRepository.save(user);  // Lưu lại thông tin người dùng
                userRepository.save(targetUser);  // Lưu lại thông tin targetUser

                return true;  // Thành công
            } else {
                throw new RuntimeException("You are not following this user");
            }
        } else {
            throw new RuntimeException("User or target user not found");
        }
    }

    @Override
    public Set<UserDTO> getFollowers(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            Set<User> followers = user.getFollowers();
            return followers.stream()
                    .map(f -> new UserDTO(f.getId(), f.getUsername(), f.getFullName(), f.getBio(), f.getAvatar(),f.getEmail()))
                    .collect(Collectors.toSet());
        } else {
            throw new RuntimeException("User not found");
        }
    }

    @Override
    public Set<UserDTO> getFollowing(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            Set<User> following = user.getFollowing();
            return following.stream()
                    .map(f -> new UserDTO(f.getId(), f.getUsername(), f.getFullName(), f.getBio(), f.getAvatar(),f.getEmail()))
                    .collect(Collectors.toSet());
        } else {
            throw new RuntimeException("User not found");
        }
    }

    @Override
    public List<UserDTO> searchUsers(String keyword) {
        // Tìm kiếm người dùng theo tên người dùng hoặc họ tên đầy đủ
        List<User> usersByUsername = userRepository.findByUsernameContainingIgnoreCase(keyword);
        List<User> usersByFullName = userRepository.findByFullNameContainingIgnoreCase(keyword);

        // Kết hợp kết quả lại để tránh trùng lặp
        List<User> users = usersByUsername.stream()
                .distinct()
                .collect(Collectors.toList());
        users.addAll(usersByFullName);

        // Chuyển đổi thông tin người dùng thành DTO
        return users.stream()
                .map(user -> {
                    Set<String> posts = user.getPosts().stream()
                            .map(post -> post.getId().toString())
                            .collect(Collectors.toSet());

                    Set<String> followers = user.getFollowers().stream()
                            .map(follower -> follower.getUsername())
                            .collect(Collectors.toSet());

                    Set<String> following = user.getFollowing().stream()
                            .map(followee -> followee.getUsername())
                            .collect(Collectors.toSet());

                    return new UserDTO(
                            user.getId(),
                            user.getUsername(),
                            user.getFullName(),
                            user.getBio(),
                            user.getAvatar(),
                            posts,
                            followers,
                            following,
                            user.getEmail()

                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        // Kiểm tra nếu người dùng có tồn tại
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Kiểm tra mật khẩu cũ có đúng không
            if (passwordEncoder.matches(oldPassword, user.getPassword())) {
                // Cập nhật mật khẩu mới sau khi mã hóa
                user.setPassword(passwordEncoder.encode(newPassword));

                // Lưu lại thông tin người dùng
                userRepository.save(user);

                return true;  // Thay đổi mật khẩu thành công
            } else {
                throw new RuntimeException("Old password is incorrect");
            }
        } else {
            throw new RuntimeException("User not found");
        }
    }

    @Override
    public boolean isEmailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    public String generateResetToken(String email) {
        // Tạo token ngẫu nhiên
        String token = UUID.randomUUID().toString();

        // Lưu token vào cơ sở dữ liệu với thời gian hết hạn
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime expiresAt = createdAt.plusHours(1);

        PasswordResetToken resetToken = new PasswordResetToken(token, email, createdAt, expiresAt);
        tokenRepository.save(resetToken);

        return token;
    }

    @Override
    public boolean validateResetToken(String token) {
        Optional<PasswordResetToken> resetToken = tokenRepository.findByToken(token);
        if (resetToken.isPresent()) {
            // Kiểm tra token có hết hạn không
            LocalDateTime expiresAt = resetToken.get().getExpiresAt();
            return expiresAt.isAfter(LocalDateTime.now());
        }
        return false;
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> resetTokenOpt = tokenRepository.findByToken(token);
        if (resetTokenOpt.isPresent()) {
            String email = resetTokenOpt.get().getEmail();
            User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        }

    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> {
                    // Tạo danh sách followers và following
                    Set<String> followers = new HashSet<>();
                    Set<String> following = new HashSet<>();
                    Set<String> posts = new HashSet<>();

                    // Lấy danh sách bài viết của người dùng
                    for (Post post : user.getPosts()) {
                        posts.add(post.getId().toString()); // Hoặc thêm bất kỳ thông tin nào bạn muốn hiển thị từ bài viết
                    }

                    // Lấy danh sách followers
                    for (User follower : user.getFollowers()) {
                        followers.add(follower.getUsername());
                    }

                    // Lấy danh sách following
                    for (User followee : user.getFollowing()) {
                        following.add(followee.getUsername());
                    }

                    // Trả về UserDTO với tất cả thông tin (bao gồm bài viết, followers, following)
                    return new UserDTO(user.getId(), user.getUsername(), user.getFullName(),
                            user.getBio(), user.getAvatar(), posts, followers, following, user.getEmail());
                })
                .collect(Collectors.toList());
    }
}