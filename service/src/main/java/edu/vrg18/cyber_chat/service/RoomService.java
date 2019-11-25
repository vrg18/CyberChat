package edu.vrg18.cyber_chat.service;

import edu.vrg18.cyber_chat.dto.RoomDto;
import edu.vrg18.cyber_chat.dto.UserDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface RoomService {

    RoomDto getRoomById(UUID id);

    RoomDto createRoom(RoomDto roomDto);

    RoomDto updateRoom(RoomDto roomDto);

    void deleteRoom(UUID id);

    Page<RoomDto> findAllRooms(int currentPage, int pageSize);

    List<RoomDto> findAllRooms();

    public List<RoomDto> findAllPublicRooms();

    Page<RoomDto> findAllRoomsOfUserAndAllPublicRooms(UserDto userDto, int currentPage, int pageSize);

    RoomDto findOrCreateTeteATeteRoom(UserDto initiatingUserDto, UserDto slaveUserDto);

    String getFullNameOfRoom(RoomDto roomDto);
}
