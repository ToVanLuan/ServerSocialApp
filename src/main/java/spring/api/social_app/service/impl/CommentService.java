package spring.api.social_app.service.impl;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spring.api.social_app.dto.CommentDTO;
import spring.api.social_app.dto.PostDTO;
import spring.api.social_app.entity.Comment;
import spring.api.social_app.entity.User;
import spring.api.social_app.entity.Post;
import spring.api.social_app.repository.CommentRepository;
import spring.api.social_app.repository.UserRepository;
import spring.api.social_app.repository.PostRepository;
import spring.api.social_app.service.ICommentService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService implements ICommentService {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Override
    public CommentDTO addComment(Long postId, Long userId, String content) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // Tạo comment
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setPost(post);
        comment.setUser(user);

        // Lưu comment vào database
        comment = commentRepository.save(comment);

        // Cập nhật số lượng comment vao bai post
        updatePostInRealtimeDatabase(post);

        // Cập nhật vào Firebase
        saveCommentToRealtimeDatabase(comment);

        return convertToDTO(comment);
    }

    // Phản hồi comment
    public CommentDTO addReply(Long commentId, Long userId, String content) {
        Comment parentComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Tạo comment trả lời
        Comment reply = new Comment();
        reply.setParent(parentComment); // Thiết lập quan hệ cha
        reply.setUser(user);
        reply.setContent(content);
        reply.setPost(parentComment.getPost()); // Gán post từ comment cha

        // Lưu trả lời vào database
        Comment savedReply = commentRepository.save(reply);

        // Cập nhật vào Firebase
        saveCommentToRealtimeDatabase(savedReply);

        // Cập nhật số lượng comment vao bai post
        updatePostInRealtimeDatabase(parentComment.getPost());

        return convertToDTO(savedReply);
    }

    @Override
    public Long countCommentsByPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return commentRepository.countByPost(post);
    }

    @Override
    public CommentDTO updateComment(Long commentId, String newContent) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setContent(newContent);
        comment = commentRepository.save(comment);
        return convertToDTO(comment);
    }

    @Override
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("Comment not found"));
        commentRepository.delete(comment);
    }

    @Override
    public List<CommentDTO> getAllCommentsByPost(Long postId) {
        List<Comment> comments = commentRepository.findByPostIdAndParentIsNull(postId);
        return comments.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public boolean likeComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (comment.getLikes().contains(user)) {
            return false; // Đã like trước đó
        }

        // Thêm user vào danh sách likes của comment
        comment.getLikes().add(user);
        commentRepository.save(comment);

        // Cập nhật vào Firebase
        saveCommentToRealtimeDatabase(comment);

        return true; // Like thành công
    }

    @Override
    public boolean unlikeComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!comment.getLikes().contains(user)) {
            return false; // Chưa like để bỏ
        }

        // Bỏ user khỏi danh sách likes của comment
        comment.getLikes().remove(user);
        commentRepository.save(comment);

        // Cập nhật vào Firebase
        saveCommentToRealtimeDatabase(comment);

        return true; // Bỏ like thành công
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
    private void saveCommentToRealtimeDatabase(Comment comment) {
        // Lấy instance của Firebase Realtime Database
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mysocialapp-1261e-default-rtdb.firebaseio.com/");

        // Tạo DatabaseReference để lưu comment vào Firebase
        DatabaseReference commentRef = database.getReference("comments").child(String.valueOf(comment.getId()));

        // Chuyển comment thành CommentDTO để lưu
        CommentDTO commentDTO = convertToDTO(comment);

        // Lưu comment vào Firebase Realtime Database
        commentRef.setValue(commentDTO, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    System.err.println("Error saving comment to Realtime Database: " + databaseError.getMessage());
                } else {
                    System.out.println("Comment successfully saved to Realtime Database");
                }
            }
        });
    }

    private CommentDTO convertToDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setUserId(comment.getUser().getId());
        dto.setUsername(comment.getUser().getUsername());
        dto.setPostId(comment.getPost().getId());
        dto.setParentId(comment.getParent() != null ? comment.getParent().getId() : null);
        dto.setReplies(comment.getReplies().stream().map(this::convertToDTO).collect(Collectors.toList()));
        dto.setLikesCount(comment.getLikes().size());

        return dto;
    }

}
