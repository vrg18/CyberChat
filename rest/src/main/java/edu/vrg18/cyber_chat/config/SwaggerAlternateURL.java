package edu.vrg18.cyber_chat.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SwaggerAlternateURL {

    /**
     * Swagger UI alternate URL
     */
    @GetMapping("/rest")
    public String swaggerUiAlternativeUrl() {
        return "redirect:/swagger-ui.html";
    }
}
