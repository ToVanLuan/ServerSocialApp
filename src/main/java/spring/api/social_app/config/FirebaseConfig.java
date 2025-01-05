package spring.api.social_app.config;
import java.io.FileInputStream;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

//@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initializeFirebase() {
        try {
            FileInputStream serviceAccount = new FileInputStream("src/main/resources/serviceAccountKey.json");

            // Cấu hình Firebase
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://mysocialapp-1261e-default-rtdb.firebaseio.com/")
                    .setStorageBucket("mysocialapp-1261e.appspot.com")
                    .build();
            // Khởi tạo Firebase App
            FirebaseApp.initializeApp(options);

        } catch (IOException e) {
            throw new RuntimeException("Could not initialize Firebase", e);
        }
    }
}

