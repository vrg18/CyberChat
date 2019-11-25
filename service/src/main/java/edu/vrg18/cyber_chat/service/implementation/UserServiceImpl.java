package edu.vrg18.cyber_chat.service.implementation;

import edu.vrg18.cyber_chat.dto.UserDto;
import edu.vrg18.cyber_chat.entity.Interlocutor;
import edu.vrg18.cyber_chat.entity.Role;
import edu.vrg18.cyber_chat.entity.Room;
import edu.vrg18.cyber_chat.entity.User;
import edu.vrg18.cyber_chat.entity.UserRole;
import edu.vrg18.cyber_chat.entity.User_;
import edu.vrg18.cyber_chat.mapper.UserMapper;
import edu.vrg18.cyber_chat.repository.FamiliarizeRepository;
import edu.vrg18.cyber_chat.repository.InterlocutorRepository;
import edu.vrg18.cyber_chat.repository.MessageRepository;
import edu.vrg18.cyber_chat.repository.RoleRepository;
import edu.vrg18.cyber_chat.repository.RoomRepository;
import edu.vrg18.cyber_chat.repository.UserRepository;
import edu.vrg18.cyber_chat.repository.UserRoleRepository;
import edu.vrg18.cyber_chat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static edu.vrg18.cyber_chat.specification.RoomSpecifications.openRoom;
import static edu.vrg18.cyber_chat.specification.RoomSpecifications.publicRoom;
import static org.springframework.data.jpa.domain.Specification.where;

@Service
@Transactional
@Resource(name = "authenticationManager")
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final InterlocutorRepository interlocutorRepository;
    private final RoomRepository roomRepository;
    private final MessageRepository messageRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final FamiliarizeRepository familiarizeRepository;
    private final UserMapper userMapper;
    private static BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Autowired
    public UserServiceImpl(UserRepository userRepository, InterlocutorRepository interlocutorRepository,
                           RoomRepository roomRepository, MessageRepository messageRepository,
                           RoleRepository roleRepository, UserRoleRepository userRoleRepository,
                           FamiliarizeRepository familiarizeRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.interlocutorRepository = interlocutorRepository;
        this.roomRepository = roomRepository;
        this.messageRepository = messageRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.familiarizeRepository = familiarizeRepository;
        this.userMapper = userMapper;
    }

    @Override
    public Optional<UserDto> getUserById(UUID id) {
        return userRepository.findById(id).map(u -> userMapper.toDto(u, false));
    }

    @Override
    public Optional<UserDto> getUserByUserName(String userName) {
        return userRepository.findUserByUserName(userName).map(u -> userMapper.toDto(u, false));
    }

    @Override
    public UserDto createUser(UserDto userDto) {

        User newUser = userMapper.toEntity(userDto);

        newUser.setEncryptedPassword(encoder.encode(userDto.getNewPassword()));

        newUser.setLastActivity(LocalDateTime.now());

        Room bazaarRoom = roomRepository.findRoomByName("Bazaar")
                .orElse(roomRepository.findAll(publicRoom()).get(0));
        if (!newUser.isBot()) newUser.setLastRoom(bazaarRoom);

        newUser = userRepository.save(newUser);

        if (!newUser.isBot()) interlocutorRepository.save(new Interlocutor(null, bazaarRoom, newUser));

        Role simpleUserRole = roleRepository.findRoleByName("ROLE_USER").get();
        userRoleRepository.save(new UserRole(null, newUser, simpleUserRole));

        return userMapper.toDto(newUser, false);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {

        User updatedUser = userMapper.toEntity(userDto);

        updatedUser.setEncryptedPassword(
                Objects.isNull(userDto.getNewPassword()) || userDto.getNewPassword().isEmpty() ?
                        userRepository.getOne(userDto.getId()).getEncryptedPassword() :
                        encoder.encode(userDto.getNewPassword()));

        if (Objects.isNull(userDto.getLastActivity())) updatedUser.setLastActivity(LocalDateTime.now());

        return userMapper.toDto(userRepository.save(updatedUser), false);
    }

    @Override
    public void deleteUser(UUID id) {

        if (roomRepository.countAllByOwnerId(id) > 0) return;
        if (messageRepository.countAllByAuthorId(id) > 0) return;

        interlocutorRepository.deleteAllByUserId(id);
        familiarizeRepository.deleteAllByUserId(id);
        userRoleRepository.deleteAllByUserId(id);
        userRepository.deleteById(id);
    }

    @Override
    public Page<UserDto> findAllUsers(int currentPage, int pageSize) {

        return userRepository.findAll(
                PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.ASC, User_.USER_NAME)))
                .map(u -> userMapper.toDto(u, true));
    }

    @Override
    public Page<UserDto> findAllUsersWithoutDisabled(int currentPage, int pageSize) {

        return userRepository.findUsersByEnabled(true,
                PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.ASC, User_.USER_NAME)))
                .map(u -> userMapper.toDto(u, false));
    }

    @Override
    public List<UserDto> findAllUsersWithoutDisabled() {

        return userRepository.findUsersByEnabled(true, Sort.by(Sort.Direction.ASC, User_.USER_NAME))
                .stream().map(u -> userMapper.toDto(u, false)).collect(Collectors.toList());
    }

    @Override
    public String getLastUserRoomId(String userName) {

        return userRepository.findUserByUserName(userName)
                .map(User::getLastRoom)
                .orElse(roomRepository.findAll(where(publicRoom().and(openRoom()))).get(0))
                .getId().toString();
    }

    @Override
    public void setLastUserRoom(UserDto userDto, UUID roomId) {

        if (Objects.isNull(userDto.getLastRoomId()) || !userDto.getLastRoomId().equals(roomId)) {
            userDto.setLastRoomId(roomId);
            updateUser(userDto);
        }
    }
}
