package edu.vrg18.cyber_chat.service.implementation;

import edu.vrg18.cyber_chat.dto.MessageDto;
import edu.vrg18.cyber_chat.dto.RoomDto;
import edu.vrg18.cyber_chat.dto.UserDto;
import edu.vrg18.cyber_chat.entity.Familiarize;
import edu.vrg18.cyber_chat.entity.Message;
import edu.vrg18.cyber_chat.entity.Message_;
import edu.vrg18.cyber_chat.entity.Room;
import edu.vrg18.cyber_chat.entity.User;
import edu.vrg18.cyber_chat.repository.FamiliarizeRepository;
import edu.vrg18.cyber_chat.repository.MessageRepository;
import edu.vrg18.cyber_chat.repository.RoomRepository;
import edu.vrg18.cyber_chat.service.MessageService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final FamiliarizeRepository familiarizeRepository;
    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository, FamiliarizeRepository familiarizeRepository,
                              RoomRepository roomRepository, ModelMapper modelMapper) {
        this.messageRepository = messageRepository;
        this.familiarizeRepository = familiarizeRepository;
        this.roomRepository = roomRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Optional<MessageDto> getMessageById(UUID id) {
        return messageRepository.findById(id).map(m -> modelMapper.map(m, MessageDto.class));
    }

    @Override
    public MessageDto createMessage(MessageDto messageDto) {
        messageDto.setDate(LocalDateTime.now());
        return modelMapper.map(messageRepository.save(modelMapper.map(messageDto, Message.class)), MessageDto.class);
    }

    @Override
    public MessageDto updateMessage(MessageDto messageDto) {
        return modelMapper.map(messageRepository.save(modelMapper.map(messageDto, Message.class)), MessageDto.class);
    }

    @Override
    public void deleteMessage(UUID id) {
        familiarizeRepository.deleteInBatch(familiarizeRepository.findAllByMessageId(id));
        messageRepository.deleteById(id);
    }

    @Override
    public Page<MessageDto> findAllMessages(Boolean increase, int currentPage, int pageSize) {

        return messageRepository.findAll(PageRequest.of(currentPage, pageSize,
                Sort.by(increase ? Sort.Direction.ASC : Sort.Direction.DESC, Message_.DATE)))
                .map(m -> modelMapper.map(m, MessageDto.class));
    }

    @Override
    public Page<MessageDto> findAllMessagesByRoomAndMarkAsRead
            (RoomDto roomDto, UserDto userDto, int currentPage, int pageSize) {

        if (currentPage < 0) {
            int countAllByRoom = messageRepository.countAllByRoom(modelMapper.map(roomDto, Room.class));
            currentPage = (countAllByRoom / pageSize) + ((countAllByRoom % pageSize) > 0 ? 1 : 0) - 1;
        }

        Page<Message> messagesPageByRoom = messageRepository.findAllByRoom(
                modelMapper.map(roomDto, Room.class),
                PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.ASC, Message_.DATE)));

        messagesPageByRoom.getContent()
                .forEach(m -> familiarizeRepository.findByMessageAndUser(m, modelMapper.map(userDto, User.class))
                        .orElseGet(() -> familiarizeRepository.save(
                                new Familiarize(null, m, modelMapper.map(userDto, User.class)))));

        return messagesPageByRoom.map(m -> modelMapper.map(m, MessageDto.class));
    }

    @Override
    public List<MessageDto> getUnreadMessagesByUserId(UUID userId) {

        List<Room> roomsByUser = roomRepository.findAllRoomByUserId(userId);

        if (roomsByUser.isEmpty()) {
            return new ArrayList<MessageDto>();
        } else {
            return messageRepository.getUserUnreadMessagesInRooms(userId, roomsByUser)
                    .stream()
                    .map(m -> modelMapper.map(m, MessageDto.class))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public boolean wasThereSuchMessageInRoom(RoomDto roomDto, String messageText) {
        return messageRepository.countAllByRoomAndText(modelMapper.map(roomDto, Room.class), messageText) != 0;
    }

    @Override
    public MessageDto newMessage(UserDto userDto, RoomDto roomDto) {
        return modelMapper.map(
                new Message(
                        null,
                        null,
                        modelMapper.map(userDto, User.class),
                        modelMapper.map(roomDto, Room.class),
                        null),
                MessageDto.class);
    }
}

