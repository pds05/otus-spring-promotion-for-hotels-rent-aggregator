package ru.otus.java.spring.project.promotion.controllers.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {

    @GetMapping("/error/403")
    public String viewError403Page() {
        return "errors/error_403";
    }
}
