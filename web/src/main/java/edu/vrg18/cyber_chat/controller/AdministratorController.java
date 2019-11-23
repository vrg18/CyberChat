package edu.vrg18.cyber_chat.controller;

import edu.vrg18.cyber_chat.dto.RoleDto;
import edu.vrg18.cyber_chat.dto.RoomDto;
import edu.vrg18.cyber_chat.dto.UserDto;
import edu.vrg18.cyber_chat.dto.UserRoleDto;
import edu.vrg18.cyber_chat.service.RoleService;
import edu.vrg18.cyber_chat.service.RoomService;
import edu.vrg18.cyber_chat.service.UserRoleService;
import edu.vrg18.cyber_chat.service.UserService;
import edu.vrg18.cyber_chat.utils.WebUtils;
import org.springframework.data.domain.Page;
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
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

        Page<UserDto> usersPage = userService.findAllUsers(uCurrentPage - 1, uPageSize);
        model.addAttribute("users", usersPage.getContent());
        int uTotalPages = usersPage.getTotalPages();
        if (uTotalPages > 0) {
            List<Integer> uPageNumbers = IntStream.rangeClosed(1, uTotalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("uPageNumbers", uPageNumbers);
            model.addAttribute("uTotalPages", uTotalPages);
            model.addAttribute("uPageSize", uPageSize);
            model.addAttribute("uCurrentPage", usersPage.getNumber() + 1);
        }

        Page<RoleDto> rolesPage = roleService.findAllRoles(rCurrentPage - 1, rPageSize);
        model.addAttribute("roles", rolesPage.getContent());
        int rTotalPages = rolesPage.getTotalPages();
        if (rTotalPages > 0) {
            List<Integer> rPageNumbers = IntStream.rangeClosed(1, rTotalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("rPageNumbers", rPageNumbers);
            model.addAttribute("rTotalPages", rTotalPages);
            model.addAttribute("rPageSize", rPageSize);
            model.addAttribute("rCurrentPage", rolesPage.getNumber() + 1);
        }

        model.addAttribute("title", "AdministratorPage");
        return "administration/administratorPage";
    }

    @GetMapping("/edit_user_admin/{id}")
    public String editUser(@PathVariable UUID id, Model model) {

        UserDto user = userService.getUserById(id).get();
        model.addAttribute("user", user);

        List<RoomDto> rooms = roomService.findAllRooms(0, 100).getContent();
        model.addAttribute("rooms", rooms);

        model.addAttribute("title", "EditUser");
        return "administration/createOrEditUserAdmin";
    }

    @PostMapping(value = "/save_user_admin", params = "id!=")
    public String updateUser(@ModelAttribute("user") UserDto user) {

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
    public String createUser(@ModelAttribute("user") UserDto user) {

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

        RoleDto role = roleService.getRoleById(id).get();
        model.addAttribute("role", role);

        model.addAttribute("title", "EditRole");
        return "administration/createOrEditRole";
    }

    @PostMapping(value = "/save_role", params = "id!=")
    public String updateRole(@ModelAttribute("role") RoleDto role) {

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
    public String createRole(@ModelAttribute("role") RoleDto role) {

        roleService.createRole(role);
        return "redirect:/administrator";
    }

    @GetMapping("/delete_role/{id}")
    public String deleteRole(@PathVariable UUID id) {

        roleService.deleteRole(id);
        return "redirect:/administrator";
    }

    @GetMapping("/new_userrole/{userId}")
    public String newUserRole(@PathVariable UUID userId, Model model) {

        UserDto selectedUser = userService.getUserById(userId).get();
        UserRoleDto newUserRole = new UserRoleDto(null, selectedUser, null);
        model.addAttribute("newUserRole", newUserRole);

        List<RoleDto> roles = roleService.findAllRoles();
        model.addAttribute("roles", roles);

        model.addAttribute("title", "NewUserRole");
        return "administration/createUserRole";
    }

    @PostMapping(value = "/save_userrole")
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
