package edu.vrg18.cyber_chat.controller;

import edu.vrg18.cyber_chat.entity.AppUser;
import edu.vrg18.cyber_chat.service.UserService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rest/users")
public class UserRestController {

    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<AppUser> listUsers() {
        return userService.findAllUsersWithoutDisabled();
    }

    @GetMapping("/{id}")
    public AppUser oneUser(@PathVariable UUID id) {
        return userService.getUserById(id).get();
    }

    @PostMapping
    public AppUser createUser(@RequestBody @Valid AppUser userDto) {
        return userService.updateUser(userDto);
    }

    @PutMapping
    public AppUser updateUser(@RequestBody @Valid AppUser userDto) {
        return userService.updateUser(userDto);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
    }
}
