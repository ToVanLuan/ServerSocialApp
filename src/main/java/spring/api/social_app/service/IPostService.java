package spring.api.social_app.service;

import org.springframework.web.multipart.MultipartFile;
import spring.api.social_app.dto.PostDTO;
import spring.api.social_app.entity.Post;

import java.util.List;

public interface IPostService {
    PostDTO createPost(PostDTO postDTO,  List<MultipartFile> imageFiles);
    boolean likePost(Long userId, Long postId);
    boolean unlikePost(Long userId, Long postId);
    int getLikeCount(Long postId);
    long getTotalPostsByUser(Long userId);
    void savePostToRealtimeDatabase(PostDTO postDTO);
    List<PostDTO> getAllPostsByUser(Long userId);
    List<PostDTO> getAllPosts();

}
