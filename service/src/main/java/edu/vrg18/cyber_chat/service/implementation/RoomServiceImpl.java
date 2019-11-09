package edu.vrg18.cyber_chat.service.implementation;

import edu.vrg18.cyber_chat.entity.AppUser;
import edu.vrg18.cyber_chat.entity.Interlocutor;
import edu.vrg18.cyber_chat.entity.Room;
import edu.vrg18.cyber_chat.repository.InterlocutorRepository;
import edu.vrg18.cyber_chat.repository.RoomRepository;
import edu.vrg18.cyber_chat.repository.UserRepository;
import edu.vrg18.cyber_chat.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.data.jpa.domain.Specification.where;
import static edu.vrg18.cyber_chat.specification.RoomSpecifications.*;

@Service
@Transactional
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final InterlocutorRepository interlocutorRepository;
    private final UserRepository userRepository;

    @Autowired
    public RoomServiceImpl(RoomRepository roomRepository,
                           InterlocutorRepository interlocutorRepository, UserRepository userRepository) {
        this.roomRepository = roomRepository;
        this.interlocutorRepository = interlocutorRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<Room> getRoomById(UUID id) {
        return roomRepository.findById(id);
    }

    @Override
    public Room createRoom(Room room) {
        room = roomRepository.save(room);
        interlocutorRepository.save(new Interlocutor(null, room, room.getOwner()));
        return room;
    }

    @Override
    public Room updateRoom(Room room) {
        return roomRepository.save(room);
    }

    @Override
    public void deleteRoom(UUID id) {
        roomRepository.deleteById(id);
    }

    @Override
    public List<Room> findAllRooms() {
        return roomRepository.findAll(new Sort(Sort.Direction.ASC, "name"));
    }

    @Override
    public List<Room> findAllRoomsOfUserAndAllOpenRooms(AppUser user) {

        return Stream.concat(
                roomRepository.findAll(where(userRoom(user).and(openRoom()))).stream(),
                roomRepository.findAll(where(publicRoom().and(openRoom()))).stream())
                .distinct()
                .sorted(Comparator.comparing(Room::getName))
                .collect(Collectors.toList());
    }

    @Override
    public Room findOrCreateTeteATeteRoom(AppUser initiatingUser, AppUser slaveUser) {

        List<Room> teteATeteRooms = roomRepository.findSharedRoomsOfTwoUsers(initiatingUser, slaveUser);
        return teteATeteRooms.stream().filter(r -> interlocutorRepository.findAllByRoomId(r.getId()).size() == 2)
                .findFirst().orElseGet(() -> createNewTeteATeteRoom(initiatingUser, slaveUser));
    }

    private Room createNewTeteATeteRoom(AppUser initiatingUser, AppUser slaveUser) {

        Room teteATeteRoom = roomRepository.save(
                new Room(null, nameOfNewTeteATeteRoom(initiatingUser, slaveUser), initiatingUser, true, false));
        initiatingUser.setLastRoom(teteATeteRoom);
        userRepository.save(initiatingUser);
        interlocutorRepository.save(new Interlocutor(null, teteATeteRoom, initiatingUser));
        interlocutorRepository.save(new Interlocutor(null, teteATeteRoom, slaveUser));
        return teteATeteRoom;
    }

    private String nameOfNewTeteATeteRoom(AppUser user1, AppUser user2) {

        return user1.getFirstName()
                .concat(" & ")
                .concat(user2.getFirstName())
                .concat(" ")
                .concat(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
    }
}
