package edu.vrg18.cyber_chat.repository;

import edu.vrg18.cyber_chat.entity.Interlocutor;
import edu.vrg18.cyber_chat.entity.Room;
import edu.vrg18.cyber_chat.entity.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface InterlocutorRepository extends JpaRepository<Interlocutor, UUID>, JpaSpecificationExecutor<Interlocutor> {

    List<Interlocutor> findAllByRoomId(UUID id);

    List<Interlocutor> findAllByRoomId(UUID id, Sort sort);

    List<Interlocutor> findAllByRoomAndUser(Room room, User user);
}
