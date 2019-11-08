package edu.vrg18.cyber_chat.service.implementation;

import edu.vrg18.cyber_chat.entity.Familiarize;
import edu.vrg18.cyber_chat.repository.FamiliarizeRepository;
import edu.vrg18.cyber_chat.service.FamiliarizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class FamiliarizeServiceImpl implements FamiliarizeService {

    private final FamiliarizeRepository familiarizeRepository;

    @Autowired
    public FamiliarizeServiceImpl(FamiliarizeRepository familiarizeRepository) {
        this.familiarizeRepository = familiarizeRepository;
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
}
