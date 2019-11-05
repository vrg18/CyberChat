package edu.vrg18.cyber_chat.service;

import edu.vrg18.cyber_chat.entity.AppUser;
import edu.vrg18.cyber_chat.entity.Interlocutor;
import edu.vrg18.cyber_chat.entity.Room;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InterlocutorService {

    Optional<Interlocutor> getInterlocutorById(UUID id);

    Interlocutor createInterlocutor(Interlocutor interlocutor);

    Interlocutor updateInterlocutor(Interlocutor interlocutor);

    void deleteInterlocutor(UUID id);

    List<Interlocutor> findAllInterlocutors();

    int getNumberInterlocutorsInRoomId(UUID id);

    boolean isUserInRoom(AppUser user, Room room);

    List<Interlocutor> findAllInterlocutorsInRoomId(UUID id);
}
