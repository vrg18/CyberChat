package edu.vrg18.cyber_chat.repository;

import edu.vrg18.cyber_chat.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findUserByUserName(String userName);

    Page<User> findUsersByEnabled(boolean enabled, Pageable page);

    List<User> findUsersByEnabled(boolean enabled, Sort sort);

    List<User> findAllByLastRoomId(UUID roomId);
}
