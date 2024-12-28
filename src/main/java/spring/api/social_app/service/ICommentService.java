package spring.api.social_app.service;

import spring.api.social_app.dto.CommentDTO;
import java.util.List;

public interface ICommentService {
    CommentDTO addComment(Long postId, Long userId, String content);
    CommentDTO updateComment(Long commentId, String newContent);
    void deleteComment(Long commentId);
    List<CommentDTO> getAllCommentsByPost(Long postId);
    boolean likeComment(Long commentId, Long userId);
    boolean unlikeComment(Long commentId, Long userId);
    CommentDTO addReply(Long commentId, Long userId, String content);
    Long countCommentsByPost(Long postId);
}
