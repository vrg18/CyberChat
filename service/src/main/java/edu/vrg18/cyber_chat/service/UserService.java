package edu.vrg18.cyber_chat.service;

import edu.vrg18.cyber_chat.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    Optional<User> getUserById(UUID id);

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(UUID id);

    Optional<User> getUserByUserName(String userName);

    List<User> findAllUsers();

    Page<User> findAllUsersWithoutDisabled(int currentPage, int pageSize);

    List<User> findUsersInRoomId(UUID id);
}
