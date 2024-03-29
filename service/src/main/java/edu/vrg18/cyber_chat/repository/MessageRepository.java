package edu.vrg18.cyber_chat.repository;

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

    List<Message> findAllByRoomId(UUID roomId);

    /**
     * Used by the bot to get a list of new messages.
     * Pagination is not needed.
     */
    @Query("SELECT m FROM Message m WHERE m.room IN (:rooms) " +
            "AND m NOT IN (SELECT f.message FROM Familiarize f WHERE f.user.id = :userId)")
    List<Message> getUserUnreadMessagesInRooms(UUID userId, List<Room> rooms);

    /**
     * Used by the bot to check for message repeatability.
     */
    int countAllByRoomAndText(Room room, String messageText);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.room.id = :roomId AND m NOT IN (SELECT f.message FROM Familiarize f WHERE f.user.id = :userId AND f.message.room.id = :roomId)")
    int countOfUnreadMessagesInRoom(UUID userId, UUID roomId);

    void deleteAllByRoomId(UUID id);

    int countAllByAuthorId(UUID id);

}
