package edu.vrg18.cyber_chat.service;

import edu.vrg18.cyber_chat.dto.RoomDto;
import edu.vrg18.cyber_chat.dto.UserDto;
import edu.vrg18.cyber_chat.entity.Room;
import edu.vrg18.cyber_chat.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface RoomService {

    RoomDto getRoomById(UUID id);

    RoomDto createRoom(RoomDto roomDto);

    RoomDto updateRoom(RoomDto roomDto);

    void deleteRoom(UUID id);

    Page<RoomDto> findAllRooms(int currentPage, int pageSize);

    public List<RoomDto> findAllPublicRooms();

    List<RoomDto> findAllRoomsOfUserAndAllPublicRooms(UserDto userDto);

    RoomDto findOrCreateTeteATeteRoom(UserDto initiatingUserDto, UserDto slaveUserDto);

    Room getRealRoomById(UUID id);

}
