package edu.vrg18.cyber_chat.service.implementation;

import edu.vrg18.cyber_chat.dto.RoleDto;
import edu.vrg18.cyber_chat.entity.Role;
import edu.vrg18.cyber_chat.entity.Role_;
import edu.vrg18.cyber_chat.repository.RoleRepository;
import edu.vrg18.cyber_chat.service.RoleService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository, ModelMapper modelMapper) {
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Optional<RoleDto> getRoleById(UUID id) {
        return roleRepository.findById(id).map(f -> modelMapper.map(f, RoleDto.class));
    }

    @Override
    public RoleDto createRole(RoleDto roleDto) {
        return modelMapper.map(roleRepository.save(modelMapper.map(roleDto, Role.class)), RoleDto.class);
    }

    @Override
    public RoleDto updateRole(RoleDto roleDto) {
        return modelMapper.map(roleRepository.save(modelMapper.map(roleDto, Role.class)), RoleDto.class);
    }

    @Override
    public void deleteRole(UUID id) {
        roleRepository.deleteById(id);
    }

    @Override
    public Page<RoleDto> findAllRoles(int currentPage, int pageSize) {
        return roleRepository.findAll(PageRequest.of(currentPage, pageSize))
                .map(r -> modelMapper.map(r, RoleDto.class));
    }

    @Override
    public List<RoleDto> findAllRoles() {
        return roleRepository.findAll().stream()
                .map(r -> modelMapper.map(r, RoleDto.class)).collect(Collectors.toList());
    }
}
