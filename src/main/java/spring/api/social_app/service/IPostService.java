package spring.api.social_app.service;


import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import spring.api.social_app.dto.PostDTO;


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
