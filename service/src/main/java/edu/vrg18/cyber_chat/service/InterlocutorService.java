package edu.vrg18.cyber_chat.service;

import edu.vrg18.cyber_chat.dto.InterlocutorDto;
import edu.vrg18.cyber_chat.dto.RoomDto;
import edu.vrg18.cyber_chat.dto.UserDto;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface InterlocutorService {

    InterlocutorDto createInterlocutor(InterlocutorDto interlocutorDto);

    void deleteInterlocutor(UUID id);

    void deleteInterlocutor(UUID roomId, UUID userId);

    boolean isUserInRoom(UserDto userDto, RoomDto roomDto);

    Page<InterlocutorDto> findAllInterlocutorsInRoomId(UUID id, int currentPage, int pageSize);
}
