package edu.vrg18.cyber_chat.service;

import edu.vrg18.cyber_chat.entity.UserRole;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRoleService {

    Optional<UserRole> getUserRoleById(UUID id);

    UserRole createUserRole(UserRole userRole);

    UserRole updateUserRole(UserRole userRole);

    void deleteUserRole(UUID id);

    List<UserRole> findAllUsersRoles();
}
