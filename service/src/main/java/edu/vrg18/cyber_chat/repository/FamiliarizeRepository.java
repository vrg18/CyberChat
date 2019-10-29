package edu.vrg18.cyber_chat.repository;

import edu.vrg18.cyber_chat.entity.AppUser;
import edu.vrg18.cyber_chat.entity.Familiarize;
import edu.vrg18.cyber_chat.entity.Message;
import edu.vrg18.cyber_chat.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FamiliarizeRepository extends JpaRepository<Familiarize, UUID> {

    @Query("SELECT f.message FROM Familiarize f WHERE f.user = :user AND f.message.room = :room")
    List<Message> findByRoomAndUser(AppUser user, Room room);

    Optional<Familiarize> findByMessageAndUser(Message message, AppUser user);
}
