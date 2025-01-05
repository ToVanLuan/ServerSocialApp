package spring.api.social_app.api;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import spring.api.social_app.dto.CommentDTO;
import spring.api.social_app.service.ICommentService;

@RestController
@RequestMapping("/api/comments")
public class CommentApi {

    @Autowired
    private ICommentService commentService;

    @PostMapping("/add")
    public ResponseEntity<CommentDTO> addComment(@RequestParam Long postId, @RequestParam Long userId, @RequestParam String content) {
        CommentDTO commentDTO = commentService.addComment(postId, userId, content);
        return ResponseEntity.ok(commentDTO);
    }
    @PostMapping("/reply")
    public ResponseEntity<CommentDTO> addReply(
            @RequestParam Long commentId,
            @RequestParam Long userId,
            @RequestParam String content) {
        CommentDTO replyDTO = commentService.addReply(commentId, userId, content);
        return ResponseEntity.ok(replyDTO);
    }

    @PutMapping("/update")
    public ResponseEntity<CommentDTO> updateComment(@RequestParam Long commentId, @RequestParam String newContent) {
        CommentDTO commentDTO = commentService.updateComment(commentId, newContent);
        return ResponseEntity.ok(commentDTO);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteComment(@RequestParam Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{postId}/get_all_comment")
    public ResponseEntity<List<CommentDTO>> getAllCommentsByPost(@PathVariable Long postId) {
        List<CommentDTO> comments = commentService.getAllCommentsByPost(postId);
        return ResponseEntity.ok(comments);
    }
    @PostMapping("/{commentId}/like")
    public ResponseEntity<Boolean> likeComment(@PathVariable Long commentId, @RequestParam Long userId) {
        boolean result = commentService.likeComment(commentId, userId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{commentId}/unlike")
    public ResponseEntity<Boolean> unlikeComment(@PathVariable Long commentId, @RequestParam Long userId) {
        boolean result = commentService.unlikeComment(commentId, userId);
        return ResponseEntity.ok(result);
    }
    @GetMapping("/posts/{postId}/comments/count")
    public Long getCommentCount(@PathVariable Long postId) {
        return commentService.countCommentsByPost(postId);
    }

}

