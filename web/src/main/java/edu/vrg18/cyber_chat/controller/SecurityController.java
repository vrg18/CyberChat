package edu.vrg18.cyber_chat.controller;

import edu.vrg18.cyber_chat.dto.UserDto;
import edu.vrg18.cyber_chat.service.UserService;
import edu.vrg18.cyber_chat.utils.WebUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class SecurityController {

    private final UserService userService;

    public SecurityController(UserService userService) {
        this.userService = userService;
    }

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
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR', 'ROLE_USER')")
    public String userInfo(Model model, Principal principal) {

        org.springframework.security.core.userdetails.User loginedUser =
                (org.springframework.security.core.userdetails.User) ((Authentication) principal).getPrincipal();
        String userInfo = WebUtils.userToString(loginedUser);
        model.addAttribute("userInfo", userInfo);

        model.addAttribute("title", "UserInfo");
        return "security/userInfoPage";
    }

    @GetMapping("/403")
    public String accessDenied(Model model, Principal principal) {

        if (principal != null) {
            org.springframework.security.core.userdetails.User loginedUser =
                    (org.springframework.security.core.userdetails.User) ((Authentication) principal).getPrincipal();
            String userInfo = WebUtils.userToString(loginedUser);
            model.addAttribute("userInfo", userInfo);
            String message = "You do not have permission!";
            model.addAttribute("message", message);
        }

        model.addAttribute("title", "Error403");
        return "security/403Page";
    }

    @GetMapping("/edit_user_own")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR', 'ROLE_USER')")
    public String editUser(Model model, Principal principal) {

        UserDto currentUser = userService.getUserByUserName(principal.getName()).get();
        model.addAttribute("user", currentUser);
        model.addAttribute("title", "EditUser");
        return "security/createOrEditUserOwn";
    }

    @PostMapping(value = "/save_user_own", params = "id!=")
    public String updateUser(@ModelAttribute("user") UserDto user, Principal principal) {

        userService.updateUser(user);
        return user.getUserName().equals(principal.getName()) ? "redirect:/" : "redirect:/logout";
    }

    @GetMapping("/new_user_own")
    public String newUserAdmin(Model model) {

        model.addAttribute("newUser", true);
        model.addAttribute("title", "NewUser");
        return "security/createOrEditUserOwn";
    }

    @PostMapping(value = "/save_user_own", params = "id=")
    public String createUser(@ModelAttribute("user") UserDto user) {

        userService.createUser(user);
        return "redirect:/";
    }
}
