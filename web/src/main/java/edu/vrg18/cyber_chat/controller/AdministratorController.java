package edu.vrg18.cyber_chat.controller;

import edu.vrg18.cyber_chat.dto.RoleDto;
import edu.vrg18.cyber_chat.dto.UserDto;
import edu.vrg18.cyber_chat.dto.UserRoleDto;
import edu.vrg18.cyber_chat.service.RoleService;
import edu.vrg18.cyber_chat.service.UserRoleService;
import edu.vrg18.cyber_chat.service.UserService;
import edu.vrg18.cyber_chat.util.PaginationAssistant;
import edu.vrg18.cyber_chat.utils.WebUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Controller
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdministratorController {

    private final UserService userService;
    private final RoleService roleService;
    private final UserRoleService userRoleService;

    public AdministratorController(UserService userService, RoleService roleService,
                                   UserRoleService userRoleService) {
        this.userService = userService;
        this.roleService = roleService;
        this.userRoleService = userRoleService;
    }

    @GetMapping("/administrator")
    public String adminPage(Model model, Principal principal,
                            @RequestParam("uPage") Optional<Integer> uPage,
                            @RequestParam("uSize") Optional<Integer> uSize,
                            @RequestParam("rPage") Optional<Integer> rPage,
                            @RequestParam("rSize") Optional<Integer> rSize) {

        int uCurrentPage = uPage.orElse(1);
        int uPageSize = uSize.orElse(10);
        int rCurrentPage = rPage.orElse(1);
        int rPageSize = rSize.orElse(10);

        org.springframework.security.core.userdetails.User loginedUser =
                (org.springframework.security.core.userdetails.User) ((Authentication) principal).getPrincipal();
        String userInfo = WebUtils.userToString(loginedUser);
        model.addAttribute("userInfo", userInfo);

        PaginationAssistant.assistant("user", model,
                userService.findAllUsers(uCurrentPage - 1, uPageSize));
        PaginationAssistant.assistant("role", model,
                roleService.findAllRoles(rCurrentPage - 1, rPageSize));

        model.addAttribute("title", "AdministratorPage");
        return "administration/administratorPage";
    }

    @GetMapping("/edit_user_admin/{id}")
    public String editUser(@PathVariable UUID id, Model model) {

        UserDto user = userService.getUserById(id).get();
        model.addAttribute("user", user);

        model.addAttribute("title", "EditUser");
        return "administration/createOrEditUserAdmin";
    }

    @GetMapping("/new_user_admin")
    public String newUserAdmin(Model model) {

        UserDto user = new UserDto();
        user.setEnabled(true);
        model.addAttribute("user", user);

        model.addAttribute("newUser", true);
        model.addAttribute("title", "NewUser");
        return "administration/createOrEditUserAdmin";
    }

    @PostMapping("/save_user_admin")
    public String createUser(@ModelAttribute("user") UserDto user) {

        if (Objects.isNull(user.getId())) userService.createUser(user);
        else userService.updateUser(user);
        return "redirect:/administrator";
    }

    @GetMapping("/delete_user/{id}")
    public String deleteUser(@PathVariable UUID id) {

        userService.deleteUser(id);
        return "redirect:/administrator";
    }

    @GetMapping("/edit_role/{id}")
    public String editRole(@PathVariable UUID id, Model model) {

        RoleDto role = roleService.getRoleById(id).get();
        model.addAttribute("role", role);

        model.addAttribute("title", "EditRole");
        return "administration/createOrEditRole";
    }

    @GetMapping("/new_role")
    public String newRole(Model model) {

        RoleDto role = new RoleDto();
        model.addAttribute("role", role);

        model.addAttribute("title", "NewRole");
        return "administration/createOrEditRole";
    }

    @PostMapping("/save_role")
    public String createRole(@ModelAttribute("role") RoleDto role) {

        if (Objects.isNull(role.getId())) roleService.createRole(role);
        else roleService.updateRole(role);
        return "redirect:/administrator";
    }

    @GetMapping("/delete_role/{id}")
    public String deleteRole(@PathVariable UUID id) {

        roleService.deleteRole(id);
        return "redirect:/administrator";
    }

    @GetMapping("/new_userrole/{userId}")
    public String newUserRole(@PathVariable UUID userId, Model model) {

        UserRoleDto newUserRole = new UserRoleDto();
        UserDto selectedUser = userService.getUserById(userId).get();
        newUserRole.setUser(selectedUser);
        model.addAttribute("newUserRole", newUserRole);

        List<RoleDto> roles = roleService.findAllRoles();
        model.addAttribute("roles", roles);

        model.addAttribute("title", "NewUserRole");
        return "administration/addRoleToUser";
    }

    @PostMapping("/save_userrole")
    public String createUserRole(@ModelAttribute("newUserRole") UserRoleDto newUserRole) {

        userRoleService.createUserRole(newUserRole);
        return "redirect:/administrator";
    }

    @GetMapping("/delete_userrole/{userId}//{roleId}")
    public String deleteUserRole(@PathVariable UUID userId, @PathVariable UUID roleId) {

        userRoleService.deleteUserRole(userId, roleId);
        return "redirect:/administrator";
    }
}
