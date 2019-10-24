package edu.vrg18.cyber_chat.service;

import edu.vrg18.cyber_chat.entity.AppUser;
import edu.vrg18.cyber_chat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<AppUser> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public AppUser createUser(AppUser user) {
        user.setEncryptedPassword(user.getNewPassword());
        user.setLastActivity(new Date());
        return userRepository.save(user);
    }

    @Override
    public AppUser updateUser(AppUser user) {
        if (!user.getNewPassword().equals("8a38aeb0-0caa-49be-8f8b-f64b6ae2ce1e")) {
            user.setEncryptedPassword(user.getNewPassword());
        }
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<AppUser> findAllUsers() {
        return userRepository.findAll();
    }
}
