package spring.api.social_app.api;
import com.google.firebase.cloud.StorageClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FirebaseTestAPI {

    @GetMapping("/test-firebase")
    public String testFirebase() {
        try {
            // Kiểm tra truy cập Firebase Storage
            String bucketName = StorageClient.getInstance().bucket().getName();
            return "Firebase Storage Bucket: " + bucketName;
        } catch (Exception e) {
            return "Firebase error: " + e.getMessage();
        }
    }
}
