package edu.vrg18.cyber_chat.service;

import edu.vrg18.cyber_chat.entity.Message;
import edu.vrg18.cyber_chat.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public Optional<Message> getMessageById(UUID id) {
        return messageRepository.findById(id);
    }

    @Override
    public Message createMessage(Message message) {
        return messageRepository.save(message);
    }

    @Override
    public Message updateMessage(Message message) {
        return messageRepository.save(message);
    }

    @Override
    public void deleteMessage(UUID id) {
        messageRepository.deleteById(id);
    }

    @Override
    public List<Message> findAllMessages() {
        return messageRepository.findAll();
    }
}
