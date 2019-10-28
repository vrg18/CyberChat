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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final InterlocutorRepository interlocutorRepository;
    private final UserRepository userRepository;

    @Autowired
    public RoomServiceImpl(RoomRepository roomRepository, InterlocutorRepository interlocutorRepository, UserRepository userRepository) {
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
        interlocutorRepository.save(new Interlocutor(null, room, room.getMaker()));
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
    public List<Room> findAllRoomsByUser(AppUser user) {

        return Stream.concat(roomRepository.findAllByConfidential(false).stream(),
                interlocutorRepository.findAllByUser(user).stream().map(Interlocutor::getRoom)).distinct()
                .filter(r -> !r.isClosed()).sorted(Comparator.comparing(Room::getName)).collect(Collectors.toList());
    }

    @Override
    public List<Room> findAllNonConfidentialRooms() {

        return roomRepository.findAllByConfidential(false);
    }

    @Override
    public Room findOrCreateTeteATeteRoom(AppUser user1, AppUser user2) {

        Room teteATeteRoom = roomRepository.save(
                new Room(null, nameOfNewTeteATeteRoom(user1, user2), user1, true, false));
        user1.setLastRoom(teteATeteRoom);
        userRepository.save(user1);
        interlocutorRepository.save(new Interlocutor(null, teteATeteRoom, user1));
        interlocutorRepository.save(new Interlocutor(null, teteATeteRoom, user2));
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
