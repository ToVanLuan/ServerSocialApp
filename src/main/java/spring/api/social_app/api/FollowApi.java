package spring.api.social_app.api;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.api.social_app.dto.UserDTO;
import spring.api.social_app.service.IUserService;

import java.util.Set;

@RestController
@RequestMapping("/api/follow")
public class FollowApi {

    @Autowired
    private IUserService userService;

    // API follow user
    @PostMapping("/{userId}/follow/{targetUserId}")
    public ResponseEntity<String> followUser(@PathVariable Long userId, @PathVariable Long targetUserId) {
        try {
            boolean success = userService.followUser(userId, targetUserId);
            if (success) {
                return ResponseEntity.ok("Followed successfully.");
            } else {
                return ResponseEntity.status(400).body("Already following this user.");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    // API unfollow user
    @PostMapping("/{userId}/unfollow/{targetUserId}")
    public ResponseEntity<String> unfollowUser(@PathVariable Long userId, @PathVariable Long targetUserId) {
        try {
            boolean success = userService.unfollowUser(userId, targetUserId);
            if (success) {
                return ResponseEntity.ok("Unfollowed successfully.");
            } else {
                return ResponseEntity.status(400).body("Not following this user.");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    // API get followers of a user
    @GetMapping("/{userId}/followers")
    public ResponseEntity<Set<UserDTO>> getFollowers(@PathVariable Long userId) {
        try {
            Set<UserDTO> followers = userService.getFollowers(userId);
            return ResponseEntity.ok(followers);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    // API get following of a user
    @GetMapping("/{userId}/following")
    public ResponseEntity<Set<UserDTO>> getFollowing(@PathVariable Long userId) {
        try {
            Set<UserDTO> following = userService.getFollowing(userId);
            return ResponseEntity.ok(following);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(null);
        }
    }
}
