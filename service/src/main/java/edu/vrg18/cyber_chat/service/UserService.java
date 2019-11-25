package edu.vrg18.cyber_chat.service;

import edu.vrg18.cyber_chat.dto.RoomDto;
import edu.vrg18.cyber_chat.dto.UserDto;
import edu.vrg18.cyber_chat.entity.Room;
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

    Page<UserDto> findAllUsers(int currentPage, int pageSize);

    Page<UserDto> findAllUsersWithoutDisabled(int currentPage, int pageSize);

    List<UserDto> findAllUsersWithoutDisabled();

    List<UserDto> findUsersInRoomId(UUID id);

    String getLastUserRoomId(String userName);

    void setLastUserRoom(UserDto userDto, UUID roomId);
}
