package edu.vrg18.cyber_chat.service;

import edu.vrg18.cyber_chat.entity.User;
import edu.vrg18.cyber_chat.entity.Room;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomService {

    Optional<Room> getRoomById(UUID id);

    Room createRoom(Room room);

    Room updateRoom(Room room);

    void deleteRoom(UUID id);

    List<Room> findAllRooms();

    List<Room> findAllRoomsOfUserAndAllOpenRooms(User user);

    Room findOrCreateTeteATeteRoom(User user1, User user2);
}
