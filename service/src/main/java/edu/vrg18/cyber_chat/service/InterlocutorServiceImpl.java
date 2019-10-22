package edu.vrg18.cyber_chat.service;

import edu.vrg18.cyber_chat.entity.Interlocutor;
import edu.vrg18.cyber_chat.repository.InterlocutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
        return interlocutorRepository.findAll();
    }
}
