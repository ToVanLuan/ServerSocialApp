package spring.api.social_app.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import jakarta.transaction.Transactional;
import spring.api.social_app.dto.PostDTO;
import spring.api.social_app.entity.Post;
import spring.api.social_app.entity.User;
import spring.api.social_app.repository.PostRepository;
import spring.api.social_app.repository.UserRepository;
import spring.api.social_app.service.IPostService;

@Service
public class PostService implements IPostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Cloudinary cloudinary;

    @Override
    public PostDTO createPost(PostDTO postDTO, List<MultipartFile> imageFiles) {
        // Kiểm tra xem người dùng có tồn tại không
        User user = userRepository.findById(postDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Upload các ảnh lên cloudiary
        List<String> imageUrls = uploadImagesPostToCloudinary(imageFiles);

        // Tạo bài viết mới
        Post post = new Post();
        post.setContent(postDTO.getContent());
        post.setImageUrls(imageUrls); // Lưu danh sách các URL từ Firebase
        post.setUser(user);

        // Lưu bài viết vào cơ sở dữ liệu
        post = postRepository.save(post);

        // Chuyển đổi sang DTO để trả về
        postDTO.setId(post.getId());
        postDTO.setUsername(user.getUsername());
        postDTO.setImageUrls(imageUrls); // Cập nhật với danh sách URL
        postDTO.setLikeCount(post.getLikes().size());
        postDTO.setCommentCount(post.getComments().size());

        // Lưu thông tin bài đăng vào Realtime Database
        savePostToRealtimeDatabase(postDTO);

        return postDTO;
    }

    public List<String> uploadImagesPostToCloudinary(List<MultipartFile> files) {
        List<String> imageUrls = new ArrayList<>();

        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("No files provided for upload");
        }

        for (MultipartFile file : files) {
            try {
                if (file == null || file.isEmpty()) {
                    throw new RuntimeException("File is empty or invalid.");
                }

                // Tải ảnh lên Cloudinary
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                        ObjectUtils.asMap(
                                "resource_type", "auto",
                                "folder", "post_images"
                        ));

                // Lấy URL an toàn (HTTPS)
                String imageUrl = (String) uploadResult.get("secure_url");
                imageUrls.add(imageUrl);

            } catch (IOException e) {
                // Ghi log lỗi và tiếp tục xử lý các tệp còn lại
                System.err.println("Failed to upload file: " + (file != null ? file.getOriginalFilename() : "unknown"));
                e.printStackTrace();
            }
        }

        if (imageUrls.isEmpty()) {
            throw new RuntimeException("Failed to upload any files to Cloudinary.");
        }

        return imageUrls;
    }


    @Override
    public void savePostToRealtimeDatabase(PostDTO postDTO) {
        // Lấy instance của Firebase Realtime Database
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mysocialapp-1261e-default-rtdb.firebaseio.com/");

        // Tạo một DatabaseReference để trỏ đến vị trí lưu trữ bài đăng trong cơ sở dữ liệu
        DatabaseReference postRef = database.getReference("posts").child(String.valueOf(postDTO.getId()));

        // Lưu thông tin bài đăng
        postRef.setValue(postDTO, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    // Xử lý khi có lỗi khi lưu dữ liệu
                    System.err.println("Error saving post to Realtime Database: " + databaseError.getMessage());
                } else {
                    // Thông báo thành công khi bài đăng được lưu
                    System.out.println("Post successfully saved to Realtime Database");
                }
            }
        });
    }


    @Override
    @Transactional
    public boolean likePost(Long userId, Long postId) {
        Optional<Post> postOpt = postRepository.findById(postId);
        Optional<User> userOpt = userRepository.findById(userId);

        if (postOpt.isPresent() && userOpt.isPresent()) {
            Post post = postOpt.get();
            User user = userOpt.get();

            // Kiểm tra xem người dùng đã thích bài viết chưa
            if (!post.getLikes().contains(user)) {
                post.getLikes().add(user);  // Thêm người dùng vào danh sách likes của bài viết
                postRepository.save(post);  // Lưu bài viết lại vào cơ sở dữ liệu

                // Cập nhật vào Realtime Database
                updatePostInRealtimeDatabase(post);

                return true;
            } else {
                throw new RuntimeException("You already liked this post");
            }
        } else {
            throw new RuntimeException("Post or User not found");
        }
    }

    @Override
    @Transactional
    public boolean unlikePost(Long userId, Long postId) {
        Optional<Post> postOpt = postRepository.findById(postId);
        Optional<User> userOpt = userRepository.findById(userId);

        if (postOpt.isPresent() && userOpt.isPresent()) {
            Post post = postOpt.get();
            User user = userOpt.get();

            // Kiểm tra xem người dùng có thích bài viết không
            if (post.getLikes().contains(user)) {
                post.getLikes().remove(user);  // Bỏ người dùng khỏi danh sách likes của bài viết
                postRepository.save(post);  // Lưu lại bài viết sau khi cập nhật

                // Cập nhật vào Realtime Database
                updatePostInRealtimeDatabase(post);

                return true;
            } else {
                throw new RuntimeException("You have not liked this post yet");
            }
        } else {
            throw new RuntimeException("Post or User not found");
        }
    }

    public void updatePostInRealtimeDatabase(Post post) {
        // Get Firebase Realtime Database instance
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mysocialapp-1261e-default-rtdb.firebaseio.com/");
        DatabaseReference postRef = database.getReference("posts").child(String.valueOf(post.getId()));

        // Create PostDTO from the Post entity
        PostDTO postDTO = new PostDTO(
                post.getId(),
                post.getContent(),
                post.getImageUrls(),
                post.getLikes().size(),
                post.getComments().size(),
                post.getUser().getUsername()
        );

        // Save the PostDTO to Firebase Realtime Database
        postRef.setValue(postDTO, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    System.err.println("Error saving post to Realtime Database: " + databaseError.getMessage());
                } else {
                    System.out.println("Post successfully saved to Realtime Database");
                }
            }
        });
    }

    // Lấy ra số lượt like bài viết
    @Override
    public int getLikeCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return post.getLikes().size();
    }

    // Lấy ra tổng số bài đăng
    @Override
    public long getTotalPostsByUser(Long userId) {
        // Kiểm tra xem người dùng có tồn tại không
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Lấy số lượng bài viết của người dùng
        return postRepository.countByUserId(userId);
    }
    @Override
    public List<PostDTO> getAllPostsByUser(Long userId) {
        // Kiểm tra xem người dùng có tồn tại không
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Lấy tất cả bài đăng của người dùng và chuyển sang PostDTO
        return postRepository.findByUserId(userId).stream()
                .map(post -> new PostDTO(
                        post.getId(),
                        post.getContent(),
                        post.getImageUrls(),
                        post.getLikes().size(),
                        post.getComments().size(),
                        post.getUser().getUsername()
                ))
                .toList();
    }
    @Override
    public List<PostDTO> getAllPosts() {
        // Lấy tất cả bài đăng từ cơ sở dữ liệu
        List<Post> posts = postRepository.findAll();

        // Chuyển đổi các bài đăng sang dạng PostDTO và trả về danh sách
        return posts.stream()
                .map(post -> new PostDTO(
                        post.getId(),
                        post.getContent(),
                        post.getImageUrls(),
                        post.getLikes().size(),
                        post.getComments().size(),
                        post.getUser().getUsername()
                ))
                .toList();
    }

}
