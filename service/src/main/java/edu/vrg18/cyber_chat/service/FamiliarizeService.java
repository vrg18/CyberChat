package edu.vrg18.cyber_chat.service;

import edu.vrg18.cyber_chat.entity.AppUser;
import edu.vrg18.cyber_chat.entity.Familiarize;
import edu.vrg18.cyber_chat.entity.Room;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FamiliarizeService {

    Optional<Familiarize> getFamiliarizeById(UUID id);
    Familiarize createFamiliarize(Familiarize familiarize);
    Familiarize updateFamiliarize(Familiarize familiarize);
    void deleteFamiliarize(UUID id);
    List<Familiarize> findAllFamiliarizes();
    String numberOfUnreadInRoom(AppUser user, Room room);
}
