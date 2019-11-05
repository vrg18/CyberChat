package edu.vrg18.cyber_chat.service.implementation;

import edu.vrg18.cyber_chat.entity.AppUser;
import edu.vrg18.cyber_chat.entity.Familiarize;
import edu.vrg18.cyber_chat.entity.Message;
import edu.vrg18.cyber_chat.entity.Room;
import edu.vrg18.cyber_chat.repository.FamiliarizeRepository;
import edu.vrg18.cyber_chat.repository.MessageRepository;
import edu.vrg18.cyber_chat.repository.RoomRepository;
import edu.vrg18.cyber_chat.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final FamiliarizeRepository familiarizeRepository;
    private final RoomRepository roomRepository;

    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository,
                              FamiliarizeRepository familiarizeRepository, RoomRepository roomRepository) {
        this.messageRepository = messageRepository;
        this.familiarizeRepository = familiarizeRepository;
        this.roomRepository = roomRepository;
    }

    @Override
    public Optional<Message> getMessageById(UUID id) {
        return messageRepository.findById(id);
    }

    @Override
    public Message createMessage(Message message) {
        message.setDate(new Date());
        return messageRepository.save(message);
    }

    @Override
    public Message updateMessage(Message message) {
        return messageRepository.save(message);
    }

    @Override
    public void deleteMessage(UUID id) {
        familiarizeRepository.deleteInBatch(familiarizeRepository.findAllByMessageId(id));
        messageRepository.deleteById(id);
    }

    @Override
    public List<Message> findAllMessages(Boolean increase) {
        return messageRepository.findAll(new Sort(increase ? Sort.Direction.ASC : Sort.Direction.DESC, "date"));
    }

    @Override
    public List<Message> findAllMessagesByRoomAndMarkAsRead(Room room, AppUser user) {
        List<Message> allMessagesByRoom = messageRepository
                .findAllByRoom(room, new Sort(Sort.Direction.ASC, "date"));
        allMessagesByRoom
                .forEach(m -> familiarizeRepository.findByMessageAndUser(m, user)
                        .orElseGet(() -> familiarizeRepository.save(new Familiarize(null, m, user))));
        return allMessagesByRoom;
    }

    @Override
    public List<Message> getUnreadMessagesByUserId(UUID userId) {
        List<Room> roomsByUser = roomRepository.findAllRoomByUserId(userId);
        return messageRepository.getUserUnreadMessagesInRooms(userId, roomsByUser);
    }

    @Override
    public boolean wasThereSuchMessageInRoom(Room room, String messageText) {
        return messageRepository.getMessagesWithSuchText(room, messageText).size() != 0;
    }
}
