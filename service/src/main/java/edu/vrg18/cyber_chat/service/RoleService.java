package edu.vrg18.cyber_chat.service;

import edu.vrg18.cyber_chat.dto.RoleDto;
import edu.vrg18.cyber_chat.entity.Role;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleService {

    Optional<Role> getRoleById(UUID id);

    Role createRole(Role role);

    Role updateRole(Role role);

    void deleteRole(UUID id);

    List<RoleDto> findAllRoles();
}
