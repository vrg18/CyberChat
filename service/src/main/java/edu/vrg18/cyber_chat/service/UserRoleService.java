package edu.vrg18.cyber_chat.service;

import edu.vrg18.cyber_chat.dto.UserRoleDto;
import edu.vrg18.cyber_chat.entity.UserRole;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRoleService {

    UserRoleDto createUserRole(UserRoleDto userRoleDto);

    void deleteUserRole(UUID userId, UUID roleId);
}
