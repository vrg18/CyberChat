package edu.vrg18.cyber_chat.repository;

import edu.vrg18.cyber_chat.entity.AppUser;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<AppUser, UUID> {

    Optional<AppUser> findAppUserByUserName(String userName);

    List<AppUser> findAppUsersByEnabled(boolean enabled, Sort sort);

    @Query("SELECT i.user FROM Interlocutor i WHERE i.room.id = :roomId " +
            "AND i.user.enabled = true ORDER BY i.user.firstName")
    List<AppUser> findAllUsersByRoomId(UUID roomId);
}
