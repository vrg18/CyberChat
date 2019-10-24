package edu.vrg18.cyber_chat.service;

import edu.vrg18.cyber_chat.entity.UserRole;
import edu.vrg18.cyber_chat.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRoleRepository userRoleRepository;

    @Autowired
    public UserRoleServiceImpl(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public Optional<UserRole> getUserRoleById(UUID id) {
        return userRoleRepository.findById(id);
    }

    @Override
    public UserRole createUserRole(UserRole userRole) {
        return userRoleRepository.save(userRole);
    }

    @Override
    public UserRole updateUserRole(UserRole userRole) {
        return userRoleRepository.save(userRole);
    }

    @Override
    public void deleteUserRole(UUID id) {
        userRoleRepository.deleteById(id);
    }

    @Override
    public List<UserRole> findAllUsersRoles() {
        return userRoleRepository.findAll();
    }
}
