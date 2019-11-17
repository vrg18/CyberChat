package edu.vrg18.cyber_chat.service.implementation;

import edu.vrg18.cyber_chat.dto.RoomDto;
import edu.vrg18.cyber_chat.dto.UserDto;
import edu.vrg18.cyber_chat.entity.Interlocutor;
import edu.vrg18.cyber_chat.entity.Room;
import edu.vrg18.cyber_chat.entity.Room_;
import edu.vrg18.cyber_chat.entity.User;
import edu.vrg18.cyber_chat.repository.InterlocutorRepository;
import edu.vrg18.cyber_chat.repository.MessageRepository;
import edu.vrg18.cyber_chat.repository.RoomRepository;
import edu.vrg18.cyber_chat.repository.UserRepository;
import edu.vrg18.cyber_chat.service.RoomService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final ModelMapper modelMapper;

    @Autowired
    public RoomServiceImpl(RoomRepository roomRepository, InterlocutorRepository interlocutorRepository,
                           UserRepository userRepository, MessageRepository messageRepository, ModelMapper roomMapper) {
        this.roomRepository = roomRepository;
        this.interlocutorRepository = interlocutorRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.modelMapper = roomMapper;
    }

    @Override
    public RoomDto getRoomById(UUID id) {
        return modelMapper.map(roomRepository.findById(id).orElse(new Room()), RoomDto.class);
    }

    @Override
    public RoomDto createRoom(RoomDto roomDto) {
        Room room = roomRepository.save(modelMapper.map(roomDto, Room.class));
        interlocutorRepository.save(new Interlocutor(null, room, room.getOwner()));
        return modelMapper.map(room, RoomDto.class);
    }

    @Override
    public RoomDto updateRoom(RoomDto roomDto) {
        return modelMapper.map(roomRepository.save(modelMapper.map(roomDto, Room.class)), RoomDto.class);
    }

    @Override
    public void deleteRoom(UUID id) {
        roomRepository.deleteById(id);
    }

    @Override
    public List<RoomDto> findAllRooms() {
        return roomRepository.findAll(new Sort(Sort.Direction.ASC, Room_.NAME))
                .stream()
                .map(r -> modelMapper.map(r, RoomDto.class))
                .peek(rd -> rd.setNumberInterlocutors(interlocutorRepository.findAllByRoomId(rd.getId()).size()))
                .collect(Collectors.toList());
    }

    @Override
    public List<RoomDto> findAllRoomsOfUserAndAllOpenRooms(UserDto userDto) {

//        List<Room> r1 = roomRepository.findAll(openRoom());
//        List<Room> r2 = roomRepository.findAll(publicRoom());
//        List<Room> r3 = roomRepository.findAll(userRoom(user));
        return Stream.concat(
                interlocutorRepository.findAll(userInterlocutor(modelMapper.map(userDto, User.class))).stream().map(Interlocutor::getRoom),
                roomRepository.findAll(where(publicRoom().and(openRoom()))).stream())
                .distinct()
                .sorted(Comparator.comparing(Room::getName))
                .map(r -> modelMapper.map(r, RoomDto.class))
                .peek(rd -> {
                    int number = messageRepository.countOfUnreadMessagesInRoom(userDto.getId(), rd.getId());
                    if (number == 0) rd.setUnreadMessages("");
                    else if (number > 9) rd.setUnreadMessages("9%2B");
                    else rd.setUnreadMessages(String.valueOf(number));
                    rd.setNumberInterlocutors(interlocutorRepository.findAllByRoomId(rd.getId()).size());
                })
                .collect(Collectors.toList());
    }

    @Override
    public RoomDto findOrCreateTeteATeteRoom(UserDto initiatingUserDto, UserDto slaveUserDto) {

        List<Room> teteATeteRooms = roomRepository.findSharedRoomsOfTwoUsers(
                modelMapper.map(initiatingUserDto, User.class),
                modelMapper.map(slaveUserDto, User.class));
        Room teteATeteRoom = teteATeteRooms
                .stream()
                .filter(r -> interlocutorRepository.findAllByRoomId(r.getId()).size() == 2)
                .findFirst()
                .orElseGet(() -> createNewTeteATeteRoom(initiatingUserDto, slaveUserDto));
        return modelMapper.map(teteATeteRoom, RoomDto.class);
    }

    private Room createNewTeteATeteRoom(UserDto initiatingUserDto, UserDto slaveUserDto) {

        User initiatingUser = modelMapper.map(initiatingUserDto, User.class);
        Room teteATeteRoom = roomRepository.save(
                new Room(null, nameOfNewTeteATeteRoom(initiatingUserDto, slaveUserDto), initiatingUser, true, false));
        initiatingUser.setLastRoom(teteATeteRoom);
        userRepository.save(initiatingUser);
        interlocutorRepository.save(new Interlocutor(null, teteATeteRoom, initiatingUser));
        interlocutorRepository.save(new Interlocutor(null, teteATeteRoom, modelMapper.map(slaveUserDto, User.class)));
        return teteATeteRoom;
    }

    private String nameOfNewTeteATeteRoom(UserDto user1, UserDto user2) {

        return user1.getFirstName()
                .concat(" & ")
                .concat(user2.getFirstName())
                .concat(" ")
                .concat(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
    }

    // Временно, при переводе всех сущностей в контроллерах на DTO уйдет.
    @Override
    public Room getRealRoomById(UUID id) {
        return roomRepository.findById(id).orElse(new Room());
    }
}
