package edu.vrg18.cyber_chat.service;

import edu.vrg18.cyber_chat.dto.RoomDto;
import edu.vrg18.cyber_chat.entity.User;
import edu.vrg18.cyber_chat.entity.Room;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomService {

    RoomDto getRoomById(UUID id);

    RoomDto createRoom(RoomDto roomDto);

    RoomDto updateRoom(RoomDto roomDto);

    void deleteRoom(UUID id);

    List<RoomDto> findAllRooms();

    List<RoomDto> findAllRoomsOfUserAndAllOpenRooms(User user);

    RoomDto findOrCreateTeteATeteRoom(User user1, User user2);

    Room getRealRoomById(UUID id);

}
