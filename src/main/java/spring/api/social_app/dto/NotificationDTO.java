package spring.api.social_app.dto;

import java.time.LocalDateTime;

public class NotificationDTO {

    private Long id;
    private String message;
    private LocalDateTime timestamp;
    private String type;

    public NotificationDTO(Long id, String message, LocalDateTime timestamp, String type) {
        this.id = id;
        this.message = message;
        this.timestamp = timestamp;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
