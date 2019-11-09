package edu.vrg18.cyber_chat.repository;

import edu.vrg18.cyber_chat.entity.User;
import edu.vrg18.cyber_chat.entity.Familiarize;
import edu.vrg18.cyber_chat.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FamiliarizeRepository extends JpaRepository<Familiarize, UUID> {

    @Query("SELECT f FROM Familiarize f WHERE f.message = :message AND f.user = :user")
    Optional<Familiarize> findByMessageAndUser(Message message, User user);

    List<Familiarize> findAllByUserId(UUID id);

    List<Familiarize> findAllByMessageId(UUID id);
}
