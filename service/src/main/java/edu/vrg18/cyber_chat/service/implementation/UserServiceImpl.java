package edu.vrg18.cyber_chat.service.implementation;

import edu.vrg18.cyber_chat.entity.AppUser;
import edu.vrg18.cyber_chat.entity.Interlocutor;
import edu.vrg18.cyber_chat.entity.Room;
import edu.vrg18.cyber_chat.repository.InterlocutorRepository;
import edu.vrg18.cyber_chat.repository.RoomRepository;
import edu.vrg18.cyber_chat.repository.UserRepository;
import edu.vrg18.cyber_chat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final InterlocutorRepository interlocutorRepository;
    private final RoomRepository roomRepository;
    private static BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Autowired
    public UserServiceImpl(UserRepository userRepository, InterlocutorRepository interlocutorRepository, RoomRepository roomRepository) {
        this.userRepository = userRepository;
        this.interlocutorRepository = interlocutorRepository;
        this.roomRepository = roomRepository;
    }

    @Override
    public Optional<AppUser> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<AppUser> getUserByUserName(String userName) {
        return userRepository.findAppUserByUserName(userName);
    }

    @Override
    public AppUser createUser(AppUser user) {
        user.setEncryptedPassword(encoder.encode(user.getNewPassword()));
        Room bazaarRoom = roomRepository.findRoomByName("Bazaar").orElse(roomRepository.findAllByConfidential(false).get(0));
        user.setLastRoom(bazaarRoom);
        user.setLastActivity(new Date());
        user = userRepository.save(user);
        interlocutorRepository.save(new Interlocutor(null, roomRepository.findRoomByName("Bazaar").get(), user));
        return user;
    }

    @Override
    public AppUser updateUser(AppUser user) {
        if (!(user.getNewPassword() == null) && !user.getNewPassword().equals("8a38aeb0-0caa-49be-8f8b-f64b6ae2ce1e")) {
            user.setEncryptedPassword(encoder.encode(user.getNewPassword()));
        }
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<AppUser> findAllUsers() {
        return userRepository.findAll(new Sort(Sort.Direction.ASC, "userName"));
    }

    @Override
    public List<String> findUsersInRoomId(UUID id) {
        return interlocutorRepository.findAllByRoomId(id).stream().map(i -> i.getUser().getFirstName()).sorted().collect(Collectors.toList());
    }
}
