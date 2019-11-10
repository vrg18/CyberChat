package edu.vrg18.cyber_chat.repository;

import edu.vrg18.cyber_chat.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {

    List<UserRole> findAllByUserId(UUID id);
}
