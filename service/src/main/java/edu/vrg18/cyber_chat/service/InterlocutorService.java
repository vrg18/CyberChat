package edu.vrg18.cyber_chat.service;

import edu.vrg18.cyber_chat.dto.InterlocutorDto;
import edu.vrg18.cyber_chat.dto.RoomDto;
import edu.vrg18.cyber_chat.dto.UserDto;
import edu.vrg18.cyber_chat.entity.Interlocutor;
import edu.vrg18.cyber_chat.entity.Room;
import edu.vrg18.cyber_chat.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InterlocutorService {

    InterlocutorDto createInterlocutor(InterlocutorDto interlocutorDto);

    void deleteInterlocutor(UUID id);

    boolean isUserInRoom(UserDto userDto, RoomDto roomDto);

    Page<InterlocutorDto> findAllInterlocutorsInRoomId(UUID id, int currentPage, int pageSize);
}
