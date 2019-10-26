package edu.vrg18.cyber_chat.service.implementation;

import edu.vrg18.cyber_chat.entity.AppUser;
import edu.vrg18.cyber_chat.entity.Interlocutor;
import edu.vrg18.cyber_chat.entity.Room;
import edu.vrg18.cyber_chat.repository.InterlocutorRepository;
import edu.vrg18.cyber_chat.service.InterlocutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InterlocutorServiceImpl implements InterlocutorService {

    private final InterlocutorRepository interlocutorRepository;

    @Autowired
    public InterlocutorServiceImpl(InterlocutorRepository interlocutorRepository) {
        this.interlocutorRepository = interlocutorRepository;
    }

    @Override
    public Optional<Interlocutor> getInterlocutorById(UUID id) {
        return interlocutorRepository.findById(id);
    }

    @Override
    public Interlocutor createInterlocutor(Interlocutor interlocutor) {
        return interlocutorRepository.save(interlocutor);
    }

    @Override
    public Interlocutor updateInterlocutor(Interlocutor interlocutor) {
        return interlocutorRepository.save(interlocutor);
    }

    @Override
    public void deleteInterlocutor(UUID id) {
        interlocutorRepository.deleteById(id);
    }

    @Override
    public List<Interlocutor> findAllInterlocutors() {
        return interlocutorRepository.findAll(new Sort(Sort.Direction.ASC, "room.name", "user.userName"));
    }

    @Override
    public List<Room> findAllRoomsByUser(AppUser user) {
        return interlocutorRepository.findAllByUser(user).stream().map(i -> i.getRoom()).sorted(Comparator.comparing(Room::getName)).collect(Collectors.toList());
    }
}
