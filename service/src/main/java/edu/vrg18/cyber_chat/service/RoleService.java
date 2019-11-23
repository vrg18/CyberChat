package edu.vrg18.cyber_chat.service;

import edu.vrg18.cyber_chat.dto.RoleDto;
import edu.vrg18.cyber_chat.entity.Role;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleService {

    Optional<RoleDto> getRoleById(UUID id);

    RoleDto createRole(RoleDto roleDto);

    RoleDto updateRole(RoleDto roleDto);

    void deleteRole(UUID id);

    Page<RoleDto> findAllRoles(int currentPage, int pageSize);

    List<RoleDto> findAllRoles();
}
