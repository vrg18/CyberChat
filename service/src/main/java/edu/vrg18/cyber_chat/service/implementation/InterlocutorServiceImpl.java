package edu.vrg18.cyber_chat.service.implementation;

import edu.vrg18.cyber_chat.dto.InterlocutorDto;
import edu.vrg18.cyber_chat.dto.RoomDto;
import edu.vrg18.cyber_chat.dto.UserDto;
import edu.vrg18.cyber_chat.entity.Interlocutor;
import edu.vrg18.cyber_chat.entity.Interlocutor_;
import edu.vrg18.cyber_chat.entity.Room;
import edu.vrg18.cyber_chat.entity.User;
import edu.vrg18.cyber_chat.entity.User_;
import edu.vrg18.cyber_chat.mapper.RoomMapper;
import edu.vrg18.cyber_chat.mapper.UserMapper;
import edu.vrg18.cyber_chat.repository.InterlocutorRepository;
import edu.vrg18.cyber_chat.service.InterlocutorService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class InterlocutorServiceImpl implements InterlocutorService {

    private final InterlocutorRepository interlocutorRepository;
    private final ModelMapper modelMapper;
    private final RoomMapper roomMapper;
    private final UserMapper userMapper;

    @Autowired
    public InterlocutorServiceImpl(InterlocutorRepository interlocutorRepository, ModelMapper modelMapper,
                                   RoomMapper roomMapper, UserMapper userMapper) {
        this.interlocutorRepository = interlocutorRepository;
        this.modelMapper = modelMapper;
        this.roomMapper = roomMapper;
        this.userMapper = userMapper;
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
    public void deleteInterlocutor(UUID roomId, UUID userId) {
        interlocutorRepository.deleteAllByRoomIdAndUserId(roomId, userId);
    }

    @Override
    public boolean isUserInRoom(UserDto userDto, RoomDto roomDto) {
        return interlocutorRepository.findAllByRoomAndUser(
                roomMapper.toEntity(roomDto),
                userMapper.toEntity(userDto))
                .size() > 0;
    }

    @Override
    public Page<InterlocutorDto> findAllInterlocutorsInRoomId(UUID id, int currentPage, int pageSize) {
        return interlocutorRepository.findAllByRoomId(id, PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.ASC,
                Interlocutor_.USER.concat(".").concat(User_.USER_NAME)))).map(f -> modelMapper.map(f, InterlocutorDto.class));
    }

}
