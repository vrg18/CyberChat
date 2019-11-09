package edu.vrg18.cyber_chat.repository;

import edu.vrg18.cyber_chat.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findUserByUserName(String userName);

    Page<User> findUsersByEnabled(boolean enabled, Pageable page);

    @Query("SELECT i.user FROM Interlocutor i WHERE i.room.id = :roomId " +
            "AND i.user.enabled = true ORDER BY i.user.firstName")
    List<User> findAllUsersByRoomId(UUID roomId);
}
