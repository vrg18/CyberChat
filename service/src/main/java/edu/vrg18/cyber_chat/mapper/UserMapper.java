package edu.vrg18.cyber_chat.mapper;

import edu.vrg18.cyber_chat.dto.RoleDto;
import edu.vrg18.cyber_chat.dto.UserDto;
import edu.vrg18.cyber_chat.entity.Room;
import edu.vrg18.cyber_chat.entity.User;
import edu.vrg18.cyber_chat.repository.RoomRepository;
import edu.vrg18.cyber_chat.repository.UserRoleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    private final RoomRepository roomRepository;
    private final UserRoleRepository userRoleRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public UserMapper(RoomRepository roomRepository, UserRoleRepository userRoleRepository, ModelMapper modelMapper) {
        this.roomRepository = roomRepository;
        this.userRoleRepository = userRoleRepository;
        this.modelMapper = modelMapper;
    }


    public User toEntity(UserDto userDto) {

        if (Objects.isNull(userDto)) {
            return null;
        } else {
            User user = modelMapper.map(userDto, User.class);
            UUID lastRoomId = userDto.getLastRoomId();
            user.setLastRoom(Objects.isNull(lastRoomId) ? null : roomRepository.getOne(userDto.getLastRoomId()));
            return user;
        }
    }

    public UserDto toDto(User user, boolean includingRoles) {

        if (Objects.isNull(user)) {
            return null;
        } else {

            UserDto userDto = modelMapper.map(user, UserDto.class);
            Room lastRoom = user.getLastRoom();
            userDto.setLastRoomId(Objects.isNull(lastRoom) ? null : lastRoom.getId());

            if (includingRoles) {
                userDto.setRoles(userRoleRepository.findAllRoleByUser(user)
                        .stream()
                        .map(r -> modelMapper.map(r, RoleDto.class))
                        .collect(Collectors.toList()));
            }

            return userDto;
        }
    }
}
