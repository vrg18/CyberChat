package edu.vrg18.cyber_chat.controller;

import edu.vrg18.cyber_chat.entity.AppUser;
import edu.vrg18.cyber_chat.entity.Room;
import edu.vrg18.cyber_chat.service.RoleService;
import edu.vrg18.cyber_chat.service.RoomService;
import edu.vrg18.cyber_chat.service.UserRoleService;
import edu.vrg18.cyber_chat.service.UserService;
import edu.vrg18.cyber_chat.utils.WebUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

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

    @GetMapping("/edit_user_own")
    public String editUser(Model model, Principal principal) {

        model.addAttribute("title", "EditUser");
        AppUser currentUser = userService.getUserByUserName(principal.getName()).get();
        model.addAttribute("user", currentUser);
        return "security/createOrEditUserOwn";
    }

    @PostMapping(value = "/save_user_own", params = "id!=")
    public String updateUser(@ModelAttribute("user") AppUser user) {

        userService.updateUser(user);
        return "redirect:/";
    }

    @GetMapping("/new_user_own")
    public String newUserAdmin(Model model) {

        model.addAttribute("title", "NewUser");
        model.addAttribute("newUser", true);
        return "security/createOrEditUserOwn";
    }

    @PostMapping(value = "/save_user_own", params = "id=")
    public String createUser(@ModelAttribute("user") AppUser user) {

        userService.createUser(user);
        return "redirect:/";
    }
}
