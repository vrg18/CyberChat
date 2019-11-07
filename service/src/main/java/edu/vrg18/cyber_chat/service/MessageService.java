package edu.vrg18.cyber_chat.service;

import edu.vrg18.cyber_chat.entity.AppUser;
import edu.vrg18.cyber_chat.entity.Message;
import edu.vrg18.cyber_chat.entity.Room;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {

    Optional<Message> getMessageById(UUID id);

    Message createMessage(Message message);

    Message updateMessage(Message message);

    void deleteMessage(UUID id);

    Page<Message> findAllMessages(Boolean increase, int currentPage, int pageSize);

    Page<Message> findAllMessagesByRoomAndMarkAsRead(Room room, AppUser user, int currentPage, int pageSize);

    List<Message> getUnreadMessagesByUserId(UUID id);

    boolean wasThereSuchMessageInRoom(Room room, String messageText);
}
