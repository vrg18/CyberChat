package edu.vrg18.cyber_chat.repository;

import edu.vrg18.cyber_chat.entity.Room;
import edu.vrg18.cyber_chat.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomRepository extends JpaRepository<Room, UUID>, JpaSpecificationExecutor<Room> {

    Optional<Room> findRoomByName(String name);

    @Query("SELECT i2.room FROM Interlocutor i2 WHERE i2.user = :user2 " +
            "AND i2.room IN (SELECT i1.room FROM Interlocutor i1 WHERE i1.user = :user1 AND i1.room.closed = false)")
    List<Room> findSharedRoomsOfTwoUsers(User user1, User user2);

    @Query("SELECT i.room FROM Interlocutor i WHERE i.user = :user AND i.room.closed = false")
    List<Room> findAllRoomsOfUser(User user);

    @Query("SELECT r FROM Room r WHERE r.confidential = false AND r.closed = false")
    List<Room> findAllPublicRooms();

    @Query("SELECT i.room FROM Interlocutor i WHERE i.user.id = :userId AND i.room.closed = false")
    List<Room> findAllRoomByUserId(UUID userId);

    int countAllByOwnerId(UUID id);
}
