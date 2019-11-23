package edu.vrg18.cyber_chat.service.implementation;

import edu.vrg18.cyber_chat.dto.InterlocutorDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    public InterlocutorDto createInterlocutor(InterlocutorDto interlocutorDto) {
        return modelMapper.map(interlocutorRepository.save(modelMapper.map(interlocutorDto, Interlocutor.class)), InterlocutorDto.class);
    }

    @Override
    public void deleteInterlocutor(UUID id) {
        interlocutorRepository.deleteById(id);
    }

    @Override
    public boolean isUserInRoom(UserDto userDto, RoomDto roomDto) {
        return interlocutorRepository.findAllByRoomAndUser(
                modelMapper.map(roomDto, Room.class),
                modelMapper.map(userDto, User.class))
                .size() > 0;
    }

    @Override
    public Page<InterlocutorDto> findAllInterlocutorsInRoomId(UUID id, int currentPage, int pageSize) {
        return interlocutorRepository.findAllByRoomId(id, PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.ASC,
                Interlocutor_.USER.concat(".").concat(User_.USER_NAME)))).map(f -> modelMapper.map(f, InterlocutorDto.class));
    }

}
