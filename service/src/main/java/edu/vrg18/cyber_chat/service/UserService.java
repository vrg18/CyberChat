package edu.vrg18.cyber_chat.service;

import edu.vrg18.cyber_chat.entity.AppUser;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    Optional<AppUser> getUserById(UUID id);
    Optional<AppUser> getUserByUserName(String userName);
    AppUser createUser(AppUser user);
    AppUser updateUser(AppUser user);
    void deleteUser(UUID id);
    List<AppUser> findAllUsers();
    List<AppUser> findAllUsersWithoutDisabled();
    List<String> findUsersInRoomId(UUID id);
}
