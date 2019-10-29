package edu.vrg18.cyber_chat.repository;

import edu.vrg18.cyber_chat.entity.AppUser;
import edu.vrg18.cyber_chat.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomRepository extends JpaRepository<Room, UUID> {

    Optional<Room> findRoomByName(String name);
    List<Room> findAllByConfidential(boolean confidential);

    @Query("SELECT i2.room FROM Interlocutor i2 WHERE i2.user = :user2 AND i2.room IN (SELECT i1.room FROM Interlocutor i1 WHERE i1.user = :user1)")
    List<Room> findSharedRoomsOfTwoUsers(AppUser user1, AppUser user2);

    @Query("SELECT i.room FROM Interlocutor i WHERE i.user = :user")
    List<Room> findAllUserRooms(AppUser user);
}
