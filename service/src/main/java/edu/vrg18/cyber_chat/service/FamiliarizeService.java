package edu.vrg18.cyber_chat.service;

import edu.vrg18.cyber_chat.entity.Familiarize;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FamiliarizeService {

    Optional<Familiarize> getFamiliarizeById(UUID id);

    Familiarize createFamiliarize(Familiarize familiarize);

    Familiarize updateFamiliarize(Familiarize familiarize);

    void deleteFamiliarize(UUID id);

    List<Familiarize> findAllFamiliarizes();
}
