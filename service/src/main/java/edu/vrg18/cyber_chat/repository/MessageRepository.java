package edu.vrg18.cyber_chat.repository;

import edu.vrg18.cyber_chat.entity.AppUser;
import edu.vrg18.cyber_chat.entity.Familiarize;
import edu.vrg18.cyber_chat.entity.Message;
import edu.vrg18.cyber_chat.entity.Room;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    List<Message> findAllByRoom(Room room);
    List<Message> findAllByRoom(Room room, Sort sort);

    @Query("SELECT m FROM Message m WHERE m.room IN (:rooms) AND m NOT IN (SELECT f.message FROM Familiarize f WHERE f.user.id = :userId)")
    List<Message> getUserUnreadMessagesInRooms(UUID userId, List<Room> rooms);

    @Query("SELECT m FROM Message m WHERE m.room = :room AND m.text = :messageText")
    List<Message> getMessagesWithSuchText(Room room, String messageText);
}
