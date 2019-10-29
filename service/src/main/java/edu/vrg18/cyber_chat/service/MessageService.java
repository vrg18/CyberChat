package edu.vrg18.cyber_chat.service;

import edu.vrg18.cyber_chat.entity.AppUser;
import edu.vrg18.cyber_chat.entity.Message;
import edu.vrg18.cyber_chat.entity.Room;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {

    Optional<Message> getMessageById(UUID id);
    Message createMessage(Message message);
    Message updateMessage(Message message);
    void deleteMessage(UUID id);
    List<Message> findAllMessages(Boolean increase);
    List<Message> findAllMessagesByRoomAndMarkAsRead(Room room, AppUser user);
}
