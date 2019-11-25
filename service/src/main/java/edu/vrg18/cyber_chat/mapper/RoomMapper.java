package edu.vrg18.cyber_chat.mapper;

import edu.vrg18.cyber_chat.dto.RoomDto;
import edu.vrg18.cyber_chat.dto.UserDto;
import edu.vrg18.cyber_chat.entity.Room;
import edu.vrg18.cyber_chat.repository.InterlocutorRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class RoomMapper {

    private final InterlocutorRepository interlocutorRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public RoomMapper(InterlocutorRepository interlocutorRepository, ModelMapper modelMapper) {
        this.interlocutorRepository = interlocutorRepository;
        this.modelMapper = modelMapper;
    }


    public Room toEntity(RoomDto roomDto) {

        if (Objects.isNull(roomDto)) {
            return null;
        } else {
            return modelMapper.map(roomDto, Room.class);
        }
    }

    public RoomDto toDto(Room room, boolean includingIterlocutors) {

        if (Objects.isNull(room)) {
            return null;
        } else {

            RoomDto roomDto = modelMapper.map(room, RoomDto.class);

            roomDto.setNumberInterlocutors(interlocutorRepository.countDistinctByRoom(room));

            if (includingIterlocutors) {
                roomDto.setUsers(interlocutorRepository.findAllUserInRoom(room)
                        .stream()
                        .map(i -> modelMapper.map(i, UserDto.class))
                        .collect(Collectors.toList()));
            }

            return roomDto;
        }
    }
}
