package edu.vrg18.cyber_chat.service;

import edu.vrg18.cyber_chat.dto.UserDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    Optional<UserDto> getUserById(UUID id);

    UserDto createUser(UserDto userDto);

    UserDto updateUser(UserDto userDto);

    void deleteUser(UUID id);

    Optional<UserDto> getUserByUserName(String userName);

    List<UserDto> findAllUsers();

    Page<UserDto> findAllUsersWithoutDisabled(int currentPage, int pageSize);

    List<UserDto> findUsersInRoomId(UUID id);

    String getLastUserRoomId(String userName);
}
