package spring.api.social_app.dto;

import java.util.Set;

public class UserDTO {

    private Long id;
    private String username;
    private String fullName;
    private String bio;
    private String avatar;
    private Set<String> posts;
    private Set<String> followers;
    private Set<String> following;

    private String email;

    public UserDTO() {
    }

    public UserDTO(Long id, String username, String fullName, String bio, String avatar, Set<String> posts, Set<String> followers, Set<String> following, String email) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.bio = bio;
        this.avatar = avatar;
        this.posts = posts;
        this.followers = followers;
        this.following = following;
        this.email = email;
    }

    public UserDTO(Long id, String username, String fullName, String bio, String avatar, String email) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.bio = bio;
        this.avatar = avatar;
        this.email = email;
    }

    // Getters và Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Set<String> getPosts() {
        return posts;
    }

    public void setPosts(Set<String> posts) {
        this.posts = posts;
    }

    public Set<String> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<String> followers) {
        this.followers = followers;
    }

    public Set<String> getFollowing() {
        return following;
    }

    public void setFollowing(Set<String> following) {
        this.following = following;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
