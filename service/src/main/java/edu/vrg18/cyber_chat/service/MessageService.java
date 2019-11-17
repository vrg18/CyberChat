package edu.vrg18.cyber_chat.service;

import edu.vrg18.cyber_chat.dto.MessageDto;
import edu.vrg18.cyber_chat.dto.RoomDto;
import edu.vrg18.cyber_chat.dto.UserDto;
import edu.vrg18.cyber_chat.entity.Message;
import edu.vrg18.cyber_chat.entity.Room;
import edu.vrg18.cyber_chat.entity.User;
import edu.vrg18.cyber_chat.util.Triple;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {

    Optional<MessageDto> getMessageById(UUID id);

    MessageDto createMessage(MessageDto messageDto);

    MessageDto updateMessage(MessageDto messageDto);

    void deleteMessage(UUID id);

    Triple<List<MessageDto>, Integer, Integer> findAllMessages(Boolean increase, int currentPage, int pageSize);

    Triple<List<MessageDto>, Integer, Integer> findAllMessagesByRoomAndMarkAsRead(RoomDto roomDto,
                                                                                  UserDto userDto,
                                                                                  int currentPage,
                                                                                  int pageSize);

    List<MessageDto> getUnreadMessagesByUserId(UUID id);

    boolean wasThereSuchMessageInRoom(RoomDto roomDto, String messageText);

    MessageDto newMessage(UserDto userDto, RoomDto roomDto);
}
