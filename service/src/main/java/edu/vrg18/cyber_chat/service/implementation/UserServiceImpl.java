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
import edu.vrg18.cyber_chat.repository.RoleRepository;
import edu.vrg18.cyber_chat.repository.RoomRepository;
import edu.vrg18.cyber_chat.repository.UserRepository;
import edu.vrg18.cyber_chat.repository.UserRoleRepository;
import edu.vrg18.cyber_chat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
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
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final FamiliarizeRepository familiarizeRepository;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private static BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Autowired
    public UserServiceImpl(UserRepository userRepository, InterlocutorRepository interlocutorRepository,
                           RoomRepository roomRepository, RoleRepository roleRepository,
                           UserRoleRepository userRoleRepository, FamiliarizeRepository familiarizeRepository,
                           UserMapper userMapper, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.interlocutorRepository = interlocutorRepository;
        this.roomRepository = roomRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.familiarizeRepository = familiarizeRepository;
        this.userMapper = userMapper;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Optional<UserDto> getUserById(UUID id) {
        return userRepository.findById(id).map(userMapper::toDto);
    }

    @Override
    public Optional<UserDto> getUserByUserName(String userName) {
//        User user = userRepository.findUserByUserName(userName).orElse(null);
//        UserDto userDto = userMapper.toDto(user);
//        if (Objects.isNull(userDto)) return Optional.empty();
//        else return Optional.of(userDto);
        return userRepository.findUserByUserName(userName).map(userMapper::toDto);
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

        return userMapper.toDto(newUser);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {

        User updatedUser = userMapper.toEntity(userDto);

        if (!(userDto.getNewPassword() == null) &&
                !userDto.getNewPassword().equals("8a38aeb0-0caa-49be-8f8b-f64b6ae2ce1e")) {
            updatedUser.setEncryptedPassword(encoder.encode(userDto.getNewPassword()));
        } else {
            updatedUser.setEncryptedPassword(userRepository.getOne(userDto.getId()).getEncryptedPassword());
        }

        if (userDto.getLastActivity() == null) updatedUser.setLastActivity(LocalDateTime.now());

        return userMapper.toDto(userRepository.save(updatedUser));
    }

    @Override
    public void deleteUser(UUID id) {

        familiarizeRepository.deleteInBatch(familiarizeRepository.findAllByUserId(id));
        userRoleRepository.deleteInBatch(userRoleRepository.findAllByUserId(id));
        userRepository.deleteById(id);
    }

    @Override
    public List<UserDto> findAllUsers() {

        return userRepository.findAll(new Sort(Sort.Direction.ASC, User_.USER_NAME))
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<UserDto> findAllUsersWithoutDisabled(int currentPage, int pageSize) {

        return userRepository.findUsersByEnabled(true,
                PageRequest.of(currentPage, pageSize, new Sort(Sort.Direction.ASC, User_.USER_NAME)))
                .map(userMapper::toDto);
    }

    @Override
    public List<UserDto> findUsersInRoomId(UUID id) {

        return userRepository.findAllUsersByRoomId(id)
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    public String getLastUserRoomId(String userName) {

        return userRepository.findUserByUserName(userName)
                .map(User::getLastRoom)
                .orElse(roomRepository.findAll(where(publicRoom().and(openRoom()))).get(0))
                .getId().toString();
    }
}
