package edu.vrg18.cyber_chat.controller;

import edu.vrg18.cyber_chat.utils.WebUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class SecurityController {

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("title", "Login");
        return "security/loginPage";
    }

    @GetMapping("/logout_successful")
    public String logoutSuccessfulPage(Model model) {
        model.addAttribute("title", "Logout");
        return "security/logoutSuccessfulPage";
    }

    @GetMapping("/user_info")
    public String userInfo(Model model, Principal principal) {

        model.addAttribute("title", "UserInfo");
        // After user login successfully.
        String userName = principal.getName();

        System.out.println("User Name: " + userName);

        User loginedUser = (User) ((Authentication) principal).getPrincipal();

        String userInfo = WebUtils.userToString(loginedUser);
        model.addAttribute("userInfo", userInfo);

        return "security/userInfoPage";
    }

    @GetMapping("/403")
    public String accessDenied(Model model, Principal principal) {

        model.addAttribute("title", "Error403");

        if (principal != null) {
            User loginedUser = (User) ((Authentication) principal).getPrincipal();
            String userInfo = WebUtils.userToString(loginedUser);
            model.addAttribute("userInfo", userInfo);
            String message = "You do not have permission!";
            model.addAttribute("message", message);
        }

        return "security/403Page";
    }
}
