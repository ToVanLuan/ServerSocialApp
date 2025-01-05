package spring.api.social_app.api;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import spring.api.social_app.dto.PostDTO;
import spring.api.social_app.service.IPostService;


@RestController
@RequestMapping("/api/posts")
public class PostApi {

    @Autowired
    private IPostService postService;

    @PostMapping("/create")
    public PostDTO createPost(@RequestParam Long userId,
                              @RequestParam String content,
                              @RequestParam List<MultipartFile> imageFiles) {
        // Chuyển đổi thông tin từ client sang DTO
        PostDTO postDTO = new PostDTO();
        postDTO.setUserId(userId);
        postDTO.setContent(content);

        // Gọi service để tạo bài viết
        return postService.createPost(postDTO, imageFiles);
    }

    // Api like bài viết
    @PostMapping("/{postId}/like")
    public ResponseEntity<String> likePost(@RequestParam Long userId, @PathVariable Long postId) {
        try {
            if (postService.likePost(userId, postId)) {
                return ResponseEntity.ok("Post liked successfully");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
        return ResponseEntity.status(400).body("Failed to like post");
    }

    // API để Bỏ Like bài viết
    @PostMapping("/{postId}/unlike")
    public ResponseEntity<String> unlikePost(@RequestParam Long userId, @PathVariable Long postId) {
        try {
            if (postService.unlikePost(userId, postId)) {
                return ResponseEntity.ok("Post unliked successfully");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
        return ResponseEntity.status(400).body("Failed to unlike post");
    }
    // Lấy tổng số like của bài viết
    @GetMapping("/{postId}/likes/count")
    public ResponseEntity<Integer> getLikeCount(@PathVariable Long postId) {
        int likeCount = postService.getLikeCount(postId);
        return ResponseEntity.ok(likeCount);
    }
    // Tổng số bài viết
    @GetMapping("/total")
    public List<PostDTO>getAllPost(){
        return postService.getAllPosts();
    }
    // Lấy ra tất cả bài viết
    @GetMapping("/user/{userId}")
    public List<PostDTO> getAllPostsByUser(@PathVariable Long userId) {
        return postService.getAllPostsByUser(userId);
    }

    // Tổng số bài viết của user
    @GetMapping("/total/{userId}")
    public long getTotalPostsByUser(@PathVariable Long userId) {
        return postService.getTotalPostsByUser(userId);
    }
}



