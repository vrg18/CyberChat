package edu.vrg18.cyber_chat.repository;

import edu.vrg18.cyber_chat.entity.AppUser;
import edu.vrg18.cyber_chat.entity.Message;
import edu.vrg18.cyber_chat.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    int countAllByRoom(Room room);

    Page<Message> findAllByRoom(Room room, Pageable page);

    @Query("SELECT m FROM Message m WHERE m.room IN (:rooms) " +
            "AND m NOT IN (SELECT f.message FROM Familiarize f WHERE f.user.id = :userId)")
    List<Message> getUserUnreadMessagesInRooms(UUID userId, List<Room> rooms);

    @Query("SELECT m FROM Message m WHERE m.room = :room AND m.text = :messageText")
    List<Message> getMessagesWithSuchText(Room room, String messageText);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.room = :room AND m NOT IN (SELECT f.message FROM Familiarize f WHERE f.user = :user AND f.message.room = :room)")
    int countOfUnreadMessagesInRoom(AppUser user, Room room);
}
