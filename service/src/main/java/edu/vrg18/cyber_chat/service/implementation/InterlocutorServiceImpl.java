package edu.vrg18.cyber_chat.service.implementation;

import edu.vrg18.cyber_chat.dto.RoomDto;
import edu.vrg18.cyber_chat.dto.UserDto;
import edu.vrg18.cyber_chat.entity.Interlocutor;
import edu.vrg18.cyber_chat.entity.Interlocutor_;
import edu.vrg18.cyber_chat.entity.Room;
import edu.vrg18.cyber_chat.entity.Room_;
import edu.vrg18.cyber_chat.entity.User;
import edu.vrg18.cyber_chat.entity.User_;
import edu.vrg18.cyber_chat.repository.InterlocutorRepository;
import edu.vrg18.cyber_chat.service.InterlocutorService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class InterlocutorServiceImpl implements InterlocutorService {

    private final InterlocutorRepository interlocutorRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public InterlocutorServiceImpl(InterlocutorRepository interlocutorRepository, ModelMapper modelMapper) {
        this.interlocutorRepository = interlocutorRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Optional<Interlocutor> getInterlocutorById(UUID id) {
        return interlocutorRepository.findById(id);
    }

    @Override
    public Interlocutor createInterlocutor(Interlocutor interlocutor) {
        return interlocutorRepository.save(interlocutor);
    }

    @Override
    public Interlocutor updateInterlocutor(Interlocutor interlocutor) {
        return interlocutorRepository.save(interlocutor);
    }

    @Override
    public void deleteInterlocutor(UUID id) {
        interlocutorRepository.deleteById(id);
    }

    @Override
    public List<Interlocutor> findAllInterlocutors() {
        return interlocutorRepository.findAll(Sort.by(Sort.Direction.ASC,
                Interlocutor_.ROOM.concat(".").concat(Room_.NAME),
                Interlocutor_.USER.concat(".").concat(User_.USER_NAME)));
    }

    @Override
    public boolean isUserInRoom(UserDto userDto, RoomDto roomDto) {
        return interlocutorRepository.findAllByRoomAndUser(
                modelMapper.map(roomDto, Room.class),
                modelMapper.map(userDto, User.class))
                .size() > 0;
    }

    @Override
    public List<Interlocutor> findAllInterlocutorsInRoomId(UUID id) {
        return interlocutorRepository.findAllByRoomId(id, Sort.by(Sort.Direction.ASC,
                Interlocutor_.USER.concat(".").concat(User_.USER_NAME)));
    }

}
