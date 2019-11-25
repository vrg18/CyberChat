package edu.vrg18.cyber_chat.repository;

import edu.vrg18.cyber_chat.entity.Interlocutor;
import edu.vrg18.cyber_chat.entity.Room;
import edu.vrg18.cyber_chat.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface InterlocutorRepository extends JpaRepository<Interlocutor, UUID>, JpaSpecificationExecutor<Interlocutor> {

    List<Interlocutor> findAllByRoomId(UUID id);

    Page<Interlocutor> findAllByRoomId(UUID id, Pageable page);

    List<Interlocutor> findAllByRoomAndUser(Room room, User user);

    @Query("SELECT i.user FROM Interlocutor i WHERE i.room = :room")
    List<User> findAllUserInRoom(Room room);

    int countDistinctByRoom(Room room);

    void deleteAllByUserId(UUID id);

    void deleteAllByRoomId(UUID id);

    void deleteAllByRoomIdAndUserId(UUID roomId, UUID userId);
}
