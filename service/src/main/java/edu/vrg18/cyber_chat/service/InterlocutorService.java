package edu.vrg18.cyber_chat.service;

import edu.vrg18.cyber_chat.entity.Interlocutor;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InterlocutorService {

    Optional<Interlocutor> getInterlocutorById(UUID id);
    Interlocutor createInterlocutor(Interlocutor interlocutor);
    Interlocutor updateInterlocutor(Interlocutor interlocutor);
    void deleteInterlocutor(UUID id);
    List<Interlocutor> findAllInterlocutors();
}
