package edu.vrg18.cyber_chat.repository;

import edu.vrg18.cyber_chat.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoomRepository extends JpaRepository<Room, UUID> {

    Optional<Room> findRoomByName(String name);
}
