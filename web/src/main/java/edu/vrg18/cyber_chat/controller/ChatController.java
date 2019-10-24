package edu.vrg18.cyber_chat.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MODERATOR','ROLE_USER')")
public class ChatController {

    @GetMapping("/")
    public String welcomePage(Model model) {
        model.addAttribute("title", "CyberChat");
        model.addAttribute("message", "Добро пожаловать в CyberChat!");
        return "chatPage";
    }
}
