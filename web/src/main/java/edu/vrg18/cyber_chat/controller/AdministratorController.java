package edu.vrg18.cyber_chat.controller;

import edu.vrg18.cyber_chat.entity.AppUser;
import edu.vrg18.cyber_chat.entity.Role;
import edu.vrg18.cyber_chat.entity.Room;
import edu.vrg18.cyber_chat.entity.UserRole;
import edu.vrg18.cyber_chat.service.RoleService;
import edu.vrg18.cyber_chat.service.RoomService;
import edu.vrg18.cyber_chat.service.UserRoleService;
import edu.vrg18.cyber_chat.service.UserService;
import edu.vrg18.cyber_chat.utils.WebUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
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

    public AdministratorController(UserService userService, RoleService roleService, UserRoleService userRoleService, RoomService roomService) {
        this.userService = userService;
        this.roleService = roleService;
        this.userRoleService = userRoleService;
        this.roomService = roomService;
    }

    @GetMapping("/administrator")
    public String adminPage(Model model, Principal principal) {

        model.addAttribute("title", "AdministratorPage");

        User loginedUser = (User) ((Authentication) principal).getPrincipal();
        String userInfo = WebUtils.userToString(loginedUser);
        model.addAttribute("userInfo", userInfo);

        List<AppUser> users = userService.findAllUsers();
        model.addAttribute("users", users);
        List<Role> roles = roleService.findAllRoles();
        model.addAttribute("roles", roles);
        List<UserRole> usersRoles = userRoleService.findAllUsersRoles();
        model.addAttribute("usersRoles", usersRoles);

        return "administration/administratorPage";
    }

    @GetMapping("/edit_user/{id}")
    public String editUser(@PathVariable UUID id, Model model) {

        model.addAttribute("title", "EditUser");
        AppUser user = userService.getUserById(id).get();
        model.addAttribute("user", user);
        List<Room> rooms = roomService.findAllRooms();
        model.addAttribute("rooms", rooms);
        return "administration/createOrEditUser";
    }

    @PostMapping(value = "/save_user", params = "id!=")
    public String updateUser(@ModelAttribute("user") AppUser user) {

        userService.updateUser(user);
        return "redirect:/administrator";
    }

    @GetMapping("/new_user")
    public String newUser(Model model) {

        model.addAttribute("title", "NewUser");
        model.addAttribute("newUser", true);
        return "administration/createOrEditUser";
    }

    @PostMapping(value = "/save_user", params = "id=")
    public String createUser(@ModelAttribute("user") AppUser user) {

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

        model.addAttribute("title", "EditRole");
        Role role = roleService.getRoleById(id).get();
        model.addAttribute("role", role);
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

        model.addAttribute("title", "EditUserRole");
        UserRole userRole = userRoleService.getUserRoleById(id).get();
        model.addAttribute("userRole", userRole);
        List<AppUser> users = userService.findAllUsers();
        model.addAttribute("users", users);
        List<Role> roles = roleService.findAllRoles();
        model.addAttribute("roles", roles);
        return "administration/createOrEditUserRole";
    }

    @PostMapping(value = "/save_userrole", params = "id!=")
    public String updateUserRole(@ModelAttribute("userRole") UserRole userRole) {

        userRoleService.updateUserRole(userRole);
        return "redirect:/administrator";
    }

    @GetMapping("/new_userrole")
    public String newUserRole(Model model) {

        model.addAttribute("title", "NewUserRole");
        List<AppUser> users = userService.findAllUsers();
        model.addAttribute("users", users);
        List<Role> roles = roleService.findAllRoles();
        model.addAttribute("roles", roles);
        model.addAttribute("newUserRole", true);
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
