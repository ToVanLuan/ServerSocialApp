package spring.api.social_app.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import spring.api.social_app.dto.TestDTO;

@RestController  // annotation

public class TestAPI {

    @PostMapping("/new")

    public TestDTO createNew(@RequestBody TestDTO model) {

        return model;

    }

    }
