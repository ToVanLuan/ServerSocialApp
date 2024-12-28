package spring.api.social_app.config;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dbrcpumvv");
        config.put("api_key", "261165594555296");
        config.put("api_secret", "2ut5K7sHKP1ifMGRsMvAxlNZK9M");
        return new Cloudinary(config);
    }
}

