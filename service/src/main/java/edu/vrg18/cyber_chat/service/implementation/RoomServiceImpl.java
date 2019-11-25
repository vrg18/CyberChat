package edu.vrg18.cyber_chat.service.implementation;

import edu.vrg18.cyber_chat.dto.RoomDto;
import edu.vrg18.cyber_chat.dto.UserDto;
import edu.vrg18.cyber_chat.entity.Interlocutor;
import edu.vrg18.cyber_chat.entity.Room;
import edu.vrg18.cyber_chat.entity.Room_;
import edu.vrg18.cyber_chat.entity.User;
import edu.vrg18.cyber_chat.mapper.RoomMapper;
import edu.vrg18.cyber_chat.mapper.UserMapper;
import edu.vrg18.cyber_chat.repository.InterlocutorRepository;
import edu.vrg18.cyber_chat.repository.MessageRepository;
import edu.vrg18.cyber_chat.repository.RoomRepository;
import edu.vrg18.cyber_chat.repository.UserRepository;
import edu.vrg18.cyber_chat.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static edu.vrg18.cyber_chat.specification.InterlocutorSpecifications.userInterlocutor;
import static edu.vrg18.cyber_chat.specification.RoomSpecifications.openRoom;
import static edu.vrg18.cyber_chat.specification.RoomSpecifications.publicRoom;
import static org.springframework.data.jpa.domain.Specification.where;

@Service
@Transactional
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final InterlocutorRepository interlocutorRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final RoomMapper roomMapper;
    private final UserMapper userMapper;

    @Autowired
    public RoomServiceImpl(RoomRepository roomRepository, InterlocutorRepository interlocutorRepository,
                           UserRepository userRepository, MessageRepository messageRepository,
                           RoomMapper roomMapper, UserMapper userMapper) {
        this.roomRepository = roomRepository;
        this.interlocutorRepository = interlocutorRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.roomMapper = roomMapper;
        this.userMapper = userMapper;
    }

    @Override
    public RoomDto getRoomById(UUID id) {
        return roomMapper.toDto(roomRepository.findById(id).orElse(new Room()), false);
    }

    @Override
    public RoomDto createRoom(RoomDto roomDto) {
        Room room = roomRepository.save(roomMapper.toEntity(roomDto));
        interlocutorRepository.save(new Interlocutor(null, room, room.getOwner()));
        return roomMapper.toDto(room, false);
    }

    @Override
    public RoomDto updateRoom(RoomDto roomDto) {
        return roomMapper.toDto(roomRepository.save(roomMapper.toEntity(roomDto)), false);
    }

    @Override
    public void deleteRoom(UUID id) {
        interlocutorRepository.deleteAllByRoomId(id);
        roomRepository.deleteById(id);
    }

    @Override
    public Page<RoomDto> findAllRooms(int currentPage, int pageSize) {
        return roomRepository.findAll(PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.ASC, Room_.NAME)))
                .map(r -> roomMapper.toDto(r, true));
    }

    @Override
    public List<RoomDto> findAllRooms() {
        return roomRepository.findAll(Sort.by(Sort.Direction.ASC, Room_.NAME))
                .stream()
                .map(r -> roomMapper.toDto(r, false))
                .collect(Collectors.toList());
    }

    @Override
    public List<RoomDto> findAllPublicRooms() {

        return roomRepository.findAll(where(publicRoom().and(openRoom())))
                .stream()
                .map(r -> roomMapper.toDto(r, false))
                .collect(Collectors.toList());
    }

    @Override
    public Page<RoomDto> findAllRoomsOfUserAndAllPublicRooms(UserDto userDto, int currentPage, int pageSize) {

//        List<Room> r1 = roomRepository.findAll(openRoom());
//        List<Room> r2 = roomRepository.findAll(publicRoom());
//        List<Room> r3 = roomRepository.findAll(userRoom(user));
//        Page<Room> roomPage = roomRepository.findAll(where(publicRoom().and(openRoom())), PageRequest.of(0, 10));

        List<Room> rooms = Stream.concat(
                interlocutorRepository.findAll(userInterlocutor(userMapper.toEntity(userDto))).stream().map(Interlocutor::getRoom),
                roomRepository.findAll(where(publicRoom().and(openRoom()))).stream())
                .distinct()
                .sorted(Comparator.comparing(Room::getName))
                .collect(Collectors.toList());

        Pageable pageable = PageRequest.of(currentPage, pageSize);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), rooms.size());

        List<RoomDto> roomsDto = rooms
                .subList(start, end)
                .stream()
                .map(r -> roomMapper.toDto(r, false))
                .peek(rd -> {
                    int number = messageRepository.countOfUnreadMessagesInRoom(userDto.getId(), rd.getId());
                    if (number == 0) rd.setUnreadMessages("");
                    else if (number > 9) rd.setUnreadMessages("9%2B");
                    else rd.setUnreadMessages(String.valueOf(number));
                })
                .collect(Collectors.toList());

        return new PageImpl<RoomDto>(roomsDto, pageable, rooms.size());
    }

    @Override
    public RoomDto findOrCreateTeteATeteRoom(UserDto initiatingUserDto, UserDto slaveUserDto) {

        List<Room> teteATeteRooms = roomRepository.findSharedRoomsOfTwoUsers(
                userMapper.toEntity(initiatingUserDto),
                userMapper.toEntity(slaveUserDto));
        Room teteATeteRoom = teteATeteRooms
                .stream()
                .filter(r -> interlocutorRepository.findAllByRoomId(r.getId()).size() == 2)
                .findFirst()
                .orElseGet(() -> createNewTeteATeteRoom(initiatingUserDto, slaveUserDto));
        return roomMapper.toDto(teteATeteRoom, false);
    }

    private Room createNewTeteATeteRoom(UserDto initiatingUserDto, UserDto slaveUserDto) {

        User initiatingUser = userMapper.toEntity(initiatingUserDto);
        Room teteATeteRoom = roomRepository.save(
                new Room(null, nameOfNewTeteATeteRoom(initiatingUserDto, slaveUserDto), initiatingUser, true, false));
        initiatingUser.setLastRoom(teteATeteRoom);
        userRepository.save(initiatingUser);
        interlocutorRepository.save(new Interlocutor(null, teteATeteRoom, initiatingUser));
        interlocutorRepository.save(new Interlocutor(null, teteATeteRoom, userMapper.toEntity(slaveUserDto)));
        return teteATeteRoom;
    }

    private String nameOfNewTeteATeteRoom(UserDto user1, UserDto user2) {

        return user1.getFirstName()
                .concat(" & ")
                .concat(user2.getFirstName())
                .concat(" ")
                .concat(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
    }

    @Override
    public String getFullNameOfRoom(RoomDto roomDto) {

        StringBuffer roomName = new StringBuffer(roomDto.getName());
        roomName.append(" (");
        interlocutorRepository.findAllByRoomId(roomDto.getId())
                .forEach(u -> roomName.append(u.getUser().getFirstName()).append(", "));
        roomName.setLength(roomName.length() - 2);
        roomName.append(")");
        return roomName.toString();
    }
}
