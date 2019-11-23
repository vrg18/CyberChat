package edu.vrg18.cyber_chat.service;

import edu.vrg18.cyber_chat.dto.FamiliarizeDto;
import edu.vrg18.cyber_chat.entity.Familiarize;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FamiliarizeService {

    Optional<FamiliarizeDto> getFamiliarizeById(UUID id);

    FamiliarizeDto createFamiliarize(FamiliarizeDto familiarizeDto);

    FamiliarizeDto updateFamiliarize(FamiliarizeDto familiarizeDto);

    void deleteFamiliarize(UUID id);

    Page<FamiliarizeDto> findAllFamiliarizes(int currentPage, int pageSize);
}
