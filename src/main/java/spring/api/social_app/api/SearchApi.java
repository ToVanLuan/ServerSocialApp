package spring.api.social_app.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import spring.api.social_app.dto.UserDTO;
import spring.api.social_app.service.IUserService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SearchApi {

    @Autowired
    private IUserService userService;

    @GetMapping("/search")
    public List<UserDTO> searchUsers(@RequestParam String keyword) {
        return userService.searchUsers(keyword);
    }
}

