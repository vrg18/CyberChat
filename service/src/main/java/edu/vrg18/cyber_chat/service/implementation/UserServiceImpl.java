package edu.vrg18.cyber_chat.service.implementation;

import edu.vrg18.cyber_chat.entity.AppUser;
import edu.vrg18.cyber_chat.entity.Familiarize;
import edu.vrg18.cyber_chat.entity.Interlocutor;
import edu.vrg18.cyber_chat.entity.Role;
import edu.vrg18.cyber_chat.entity.Room;
import edu.vrg18.cyber_chat.entity.UserRole;
import edu.vrg18.cyber_chat.repository.FamiliarizeRepository;
import edu.vrg18.cyber_chat.repository.InterlocutorRepository;
import edu.vrg18.cyber_chat.repository.RoleRepository;
import edu.vrg18.cyber_chat.repository.RoomRepository;
import edu.vrg18.cyber_chat.repository.UserRepository;
import edu.vrg18.cyber_chat.repository.UserRoleRepository;
import edu.vrg18.cyber_chat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final InterlocutorRepository interlocutorRepository;
    private final RoomRepository roomRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final FamiliarizeRepository familiarizeRepository;
    private static BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Autowired
    public UserServiceImpl(UserRepository userRepository, InterlocutorRepository interlocutorRepository,
                           RoomRepository roomRepository, RoleRepository roleRepository,
                           UserRoleRepository userRoleRepository, FamiliarizeRepository familiarizeRepository) {
        this.userRepository = userRepository;
        this.interlocutorRepository = interlocutorRepository;
        this.roomRepository = roomRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.familiarizeRepository = familiarizeRepository;
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
        user.setLastActivity(new Date());
        Room bazaarRoom = roomRepository.findRoomByName("Bazaar")
                .orElse(roomRepository.findAllByConfidential(false).get(0));
        if (!user.isBot()) user.setLastRoom(bazaarRoom);
        user = userRepository.save(user);
        if (!user.isBot()) interlocutorRepository.save(new Interlocutor(null, bazaarRoom, user));
        Role simpleUserRole = roleRepository.findRoleByName("ROLE_USER").get();
        userRoleRepository.save(new UserRole(null, user, simpleUserRole));
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
        familiarizeRepository.deleteInBatch(familiarizeRepository.findAllByUserId(id));
        userRepository.deleteById(id);
    }

    @Override
    public List<AppUser> findAllUsers() {
        return userRepository.findAll(new Sort(Sort.Direction.ASC, "userName"));
    }

    @Override
    public List<AppUser> findAllUsersWithoutDisabled() {
        return userRepository.findAppUsersByEnabled(true, new Sort(Sort.Direction.ASC, "userName"));
    }

    @Override
    public List<AppUser> findUsersInRoomId(UUID id) {
        return userRepository.findAllUsersByRoomId(id);
    }
}
