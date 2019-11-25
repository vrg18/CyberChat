package edu.vrg18.cyber_chat.service;

import edu.vrg18.cyber_chat.dto.UserRoleDto;

import java.util.UUID;

public interface UserRoleService {

    UserRoleDto createUserRole(UserRoleDto userRoleDto);

    void deleteUserRole(UUID userId, UUID roleId);
}
