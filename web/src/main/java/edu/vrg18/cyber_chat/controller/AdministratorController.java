package edu.vrg18.cyber_chat.controller;

import edu.vrg18.cyber_chat.dto.RoomDto;
import edu.vrg18.cyber_chat.entity.Role;
import edu.vrg18.cyber_chat.entity.User;
import edu.vrg18.cyber_chat.entity.UserRole;
import edu.vrg18.cyber_chat.service.RoleService;
import edu.vrg18.cyber_chat.service.RoomService;
import edu.vrg18.cyber_chat.service.UserRoleService;
import edu.vrg18.cyber_chat.service.UserService;
import edu.vrg18.cyber_chat.utils.WebUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Controller
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdministratorController {

    private final UserService userService;
    private final RoleService roleService;
    private final UserRoleService userRoleService;
    private final RoomService roomService;

    public AdministratorController(UserService userService, RoleService roleService,
                                   UserRoleService userRoleService, RoomService roomService) {
        this.userService = userService;
        this.roleService = roleService;
        this.userRoleService = userRoleService;
        this.roomService = roomService;
    }

    @GetMapping("/administrator")
    public String adminPage(Model model, Principal principal) {

        org.springframework.security.core.userdetails.User loginedUser =
                (org.springframework.security.core.userdetails.User) ((Authentication) principal).getPrincipal();
        String userInfo = WebUtils.userToString(loginedUser);
        model.addAttribute("userInfo", userInfo);

        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);

        List<Role> roles = roleService.findAllRoles();
        model.addAttribute("roles", roles);

        List<UserRole> usersRoles = userRoleService.findAllUsersRoles();
        model.addAttribute("usersRoles", usersRoles);

        model.addAttribute("title", "AdministratorPage");
        return "administration/administratorPage";
    }

    @GetMapping("/edit_user_admin/{id}")
    public String editUser(@PathVariable UUID id, Model model) {

        User user = userService.getUserById(id).get();
        model.addAttribute("user", user);

        List<RoomDto> rooms = roomService.findAllRooms();
        model.addAttribute("rooms", rooms);

        model.addAttribute("title", "EditUser");
        return "administration/createOrEditUserAdmin";
    }

    @PostMapping(value = "/save_user_admin", params = "id!=")
    public String updateUser(@ModelAttribute("user") User user) {

        userService.updateUser(user);
        return "redirect:/administrator";
    }

    @GetMapping("/new_user_admin")
    public String newUserAdmin(Model model) {

        model.addAttribute("newUser", true);
        model.addAttribute("title", "NewUser");
        return "administration/createOrEditUserAdmin";
    }

    @PostMapping(value = "/save_user_admin", params = "id=")
    public String createUser(@ModelAttribute("user") User user) {

        userService.createUser(user);
        return "redirect:/administrator";
    }

    @GetMapping("/delete_user/{id}")
    public String deleteUser(@PathVariable UUID id) {

        userService.deleteUser(id);
        return "redirect:/administrator";
    }

    @GetMapping("/edit_role/{id}")
    public String editRole(@PathVariable UUID id, Model model) {

        Role role = roleService.getRoleById(id).get();
        model.addAttribute("role", role);

        model.addAttribute("title", "EditRole");
        return "administration/createOrEditRole";
    }

    @PostMapping(value = "/save_role", params = "id!=")
    public String updateRole(@ModelAttribute("role") Role role) {

        roleService.updateRole(role);
        return "redirect:/administrator";
    }

    @GetMapping("/new_role")
    public String newRole(Model model) {

        model.addAttribute("title", "NewRole");
        model.addAttribute("newRole", true);
        return "administration/createOrEditRole";
    }

    @PostMapping(value = "/save_role", params = "id=")
    public String createRole(@ModelAttribute("role") Role role) {

        roleService.createRole(role);
        return "redirect:/administrator";
    }

    @GetMapping("/delete_role/{id}")
    public String deleteRole(@PathVariable UUID id) {

        roleService.deleteRole(id);
        return "redirect:/administrator";
    }

    @GetMapping("/edit_userrole/{id}")
    public String editUserRole(@PathVariable UUID id, Model model) {

        UserRole userRole = userRoleService.getUserRoleById(id).get();
        model.addAttribute("userRole", userRole);

        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);

        List<Role> roles = roleService.findAllRoles();
        model.addAttribute("roles", roles);

        model.addAttribute("title", "EditUserRole");
        return "administration/createOrEditUserRole";
    }

    @PostMapping(value = "/save_userrole", params = "id!=")
    public String updateUserRole(@ModelAttribute("userRole") UserRole userRole) {

        userRoleService.updateUserRole(userRole);
        return "redirect:/administrator";
    }

    @GetMapping("/new_userrole")
    public String newUserRole(Model model) {

        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);

        List<Role> roles = roleService.findAllRoles();
        model.addAttribute("roles", roles);

        model.addAttribute("newUserRole", true);
        model.addAttribute("title", "NewUserRole");
        return "administration/createOrEditUserRole";
    }

    @PostMapping(value = "/save_userrole", params = "id=")
    public String createUserRole(@ModelAttribute("userRole") UserRole userRole) {

        userRoleService.createUserRole(userRole);
        return "redirect:/administrator";
    }

    @GetMapping("/delete_userrole/{id}")
    public String deleteUserRole(@PathVariable UUID id) {

        userRoleService.deleteUserRole(id);
        return "redirect:/administrator";
    }
}
