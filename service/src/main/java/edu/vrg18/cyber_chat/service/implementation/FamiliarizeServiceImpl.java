package edu.vrg18.cyber_chat.service.implementation;

import edu.vrg18.cyber_chat.entity.AppUser;
import edu.vrg18.cyber_chat.entity.Familiarize;
import edu.vrg18.cyber_chat.entity.Message;
import edu.vrg18.cyber_chat.entity.Room;
import edu.vrg18.cyber_chat.repository.FamiliarizeRepository;
import edu.vrg18.cyber_chat.repository.MessageRepository;
import edu.vrg18.cyber_chat.service.FamiliarizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FamiliarizeServiceImpl implements FamiliarizeService {

    private final FamiliarizeRepository familiarizeRepository;
    private final MessageRepository messageRepository;

    @Autowired
    public FamiliarizeServiceImpl(FamiliarizeRepository familiarizeRepository, MessageRepository messageRepository) {
        this.familiarizeRepository = familiarizeRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public Optional<Familiarize> getFamiliarizeById(UUID id) {
        return familiarizeRepository.findById(id);
    }

    @Override
    public Familiarize createFamiliarize(Familiarize familiarize) {
        return familiarizeRepository.save(familiarize);
    }

    @Override
    public Familiarize updateFamiliarize(Familiarize familiarize) {
        return familiarizeRepository.save(familiarize);
    }

    @Override
    public void deleteFamiliarize(UUID id) {
        familiarizeRepository.deleteById(id);
    }

    @Override
    public List<Familiarize> findAllFamiliarizes() {
        return familiarizeRepository.findAll(new Sort(Sort.Direction.ASC, "room.name", "user.userName"));
    }

    @Override
    public String numberOfUnreadInRoom(AppUser user, Room room) {
        List<Message> readMessage = familiarizeRepository.findByRoomAndUser(user, room);
        long number = messageRepository.findAllByRoom(room).stream().filter(all -> !readMessage.contains(all)).count();
        if (number == 0) return "";
        else if (number > 9) return "9%2B";
        else return String.valueOf(number);
    }
}
