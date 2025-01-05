package spring.api.social_app.api;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import spring.api.social_app.dto.UserDTO;
import spring.api.social_app.service.IUserService;


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

