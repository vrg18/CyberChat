package edu.vrg18.cyber_chat.service.implementation;

import edu.vrg18.cyber_chat.dto.UserRoleDto;
import edu.vrg18.cyber_chat.entity.UserRole;
import edu.vrg18.cyber_chat.repository.UserRoleRepository;
import edu.vrg18.cyber_chat.service.UserRoleService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRoleRepository userRoleRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public UserRoleServiceImpl(UserRoleRepository userRoleRepository, ModelMapper modelMapper) {
        this.userRoleRepository = userRoleRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public UserRoleDto createUserRole(UserRoleDto userRoleDto) {
        return modelMapper.map(userRoleRepository.save(modelMapper.map(userRoleDto, UserRole.class)), UserRoleDto.class);
    }

    @Override
    public void deleteUserRole(UUID userId, UUID roleId) {
        userRoleRepository.deleteAllByUserIdAndRoleId(userId, roleId);
    }
}
