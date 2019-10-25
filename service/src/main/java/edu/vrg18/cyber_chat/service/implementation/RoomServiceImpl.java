package edu.vrg18.cyber_chat.service.implementation;

import edu.vrg18.cyber_chat.entity.Interlocutor;
import edu.vrg18.cyber_chat.entity.Room;
import edu.vrg18.cyber_chat.repository.InterlocutorRepository;
import edu.vrg18.cyber_chat.repository.RoomRepository;
import edu.vrg18.cyber_chat.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final InterlocutorRepository interlocutorRepository;

    @Autowired
    public RoomServiceImpl(RoomRepository roomRepository, InterlocutorRepository interlocutorRepository) {
        this.roomRepository = roomRepository;
        this.interlocutorRepository = interlocutorRepository;
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
}
