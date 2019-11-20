package edu.vrg18.cyber_chat.repository;

import edu.vrg18.cyber_chat.entity.Role;
import edu.vrg18.cyber_chat.entity.User;
import edu.vrg18.cyber_chat.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {

    List<UserRole> findAllByUserId(UUID id);

    List<UserRole> findAllByUserIdAndRoleId(UUID userId, UUID roleId);

    @Query("SELECT ur.role FROM UserRole ur WHERE ur.user = :user")
    List<Role> findAllRoleByUser(User user);
}
