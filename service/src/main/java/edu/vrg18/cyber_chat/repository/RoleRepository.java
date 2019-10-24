package edu.vrg18.cyber_chat.repository;

import edu.vrg18.cyber_chat.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
}
