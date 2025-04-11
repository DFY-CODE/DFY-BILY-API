package one.dfy.bily.api.common.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:3000") // React 개발 서버의 URL
public class AppController {

    private final ResourceLoader resourceLoader;

    @Autowired
    public AppController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @GetMapping("/api/data")
    public String getData() {
        return "Hello from Spring Boot!";
    }

}
