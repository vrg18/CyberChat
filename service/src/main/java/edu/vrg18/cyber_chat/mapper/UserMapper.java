package edu.vrg18.cyber_chat.mapper;

import edu.vrg18.cyber_chat.dto.UserDto;
import edu.vrg18.cyber_chat.entity.Room;
import edu.vrg18.cyber_chat.entity.User;
import edu.vrg18.cyber_chat.repository.RoomRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
public class UserMapper {

    private final RoomRepository roomRepository;
    private final ModelMapper userMapper;

    @Autowired
    public UserMapper(RoomRepository roomRepository, ModelMapper userMapper) {
        this.roomRepository = roomRepository;
        this.userMapper = userMapper;
    }


    public User toEntity(UserDto userDto) {

        if (Objects.isNull(userDto)) {
            return null;
        }
        else {
            User user = userMapper.map(userDto, User.class);
            UUID lastRoomId = userDto.getLastRoomId();
            user.setLastRoom(Objects.isNull(lastRoomId) ? null : roomRepository.getOne(userDto.getLastRoomId()));
            return user;
        }
    }

    public UserDto toDto(User user) {

        if (Objects.isNull(user)) {
            return null;
        }
        else {
            UserDto userDto = userMapper.map(user, UserDto.class);
            Room lastRoom = user.getLastRoom();
            userDto.setLastRoomId(Objects.isNull(lastRoom) ? null : lastRoom.getId());
            return userDto;
        }
    }
}
