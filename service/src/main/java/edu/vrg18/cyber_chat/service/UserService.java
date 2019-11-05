package edu.vrg18.cyber_chat.service;

import edu.vrg18.cyber_chat.entity.AppUser;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    Optional<AppUser> getUserById(UUID id);

    AppUser createUser(AppUser user);

    AppUser updateUser(AppUser user);

    void deleteUser(UUID id);

    Optional<AppUser> getUserByUserName(String userName);

    List<AppUser> findAllUsers();

    List<AppUser> findAllUsersWithoutDisabled();

    List<AppUser> findUsersInRoomId(UUID id);
}
