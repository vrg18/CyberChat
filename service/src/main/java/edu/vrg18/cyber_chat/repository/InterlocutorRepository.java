package edu.vrg18.cyber_chat.repository;

import edu.vrg18.cyber_chat.entity.AppUser;
import edu.vrg18.cyber_chat.entity.Interlocutor;
import edu.vrg18.cyber_chat.entity.Room;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InterlocutorRepository extends JpaRepository<Interlocutor, UUID> {

//    List<Interlocutor> findAllByUser(AppUser user);
    List<Interlocutor> findAllByRoomId(UUID id);
    List<Interlocutor> findAllByRoomId(UUID id, Sort sort);
    List<Interlocutor> findAllByRoomAndUser(Room room, AppUser user);
}
