package spring.api.social_app.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String fullName;
    private String password;
    private String bio;
    private String avatar;

    @Column(unique = true)
    private String email;


    // Quan hệ với Post (Một người dùng có thể có nhiều bài viết)
    @OneToMany(mappedBy = "user")
    private Set<Post> posts = new HashSet<>();

    // Quan hệ ManyToMany với người theo dõi (Một người dùng có thể có nhiều người theo dõi và theo dõi nhiều người)
    @ManyToMany
    @JoinTable(
            name = "user_followers",  // Tên bảng liên kết
            joinColumns = @JoinColumn(name = "user_id"),  // Cột khóa ngoại tham chiếu đến người dùng
            inverseJoinColumns = @JoinColumn(name = "follower_id")  // Cột khóa ngoại tham chiếu đến người theo dõi
    )
    private Set<User> followers = new HashSet<>();

    // Quan hệ ManyToMany ngược lại, theo dõi những người dùng khác
    @ManyToMany(mappedBy = "followers")
    private Set<User> following = new HashSet<>();

    // Quan hệ với Comment (Một người dùng có thể có nhiều comment)
    @OneToMany(mappedBy = "user")
    private Set<Comment> comments = new HashSet<>();

    // Quan hệ với Notification (Một người dùng có thể nhận nhiều thông báo)
    @OneToMany(mappedBy = "user")
    private Set<Notification> notifications = new HashSet<>();

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Post> getPosts() {
        return posts;
    }

    public void setPosts(Set<Post> posts) {
        this.posts = posts;
    }

    public Set<User> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<User> followers) {
        this.followers = followers;
    }

    public Set<User> getFollowing() {
        return following;
    }

    public void setFollowing(Set<User> following) {
        this.following = following;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public Set<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(Set<Notification> notifications) {
        this.notifications = notifications;
    }
}
