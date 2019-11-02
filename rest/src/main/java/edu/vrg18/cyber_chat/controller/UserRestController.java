package edu.vrg18.cyber_chat.controller;

import edu.vrg18.cyber_chat.entity.AppUser;
import edu.vrg18.cyber_chat.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/rest/users")
@PreAuthorize("hasRole('ROLE_USER')")
public class UserRestController {

    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<AppUser> oneUser(@PathVariable UUID id) {
        return
                userService.getUserById(id).map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    //    @GetMapping(value = "/name/{userName}", produces = "application/json")
    @GetMapping("/name/{userName}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<AppUser> oneUserByName(@PathVariable String userName) {
        return
                userService.getUserByUserName(userName).map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @PreAuthorize("permitAll()")
    public AppUser createUser(@RequestBody @Valid AppUser user) {
        user.setBot(true);
        return userService.createUser(user);
    }

    @PutMapping
    public AppUser updateUser(@RequestBody @Valid AppUser user) {
        user.setBot(true);
        return userService.updateUser(user);
    }
}
