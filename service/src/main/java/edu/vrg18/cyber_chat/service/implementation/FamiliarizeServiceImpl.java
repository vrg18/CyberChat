package edu.vrg18.cyber_chat.service.implementation;

import edu.vrg18.cyber_chat.dto.FamiliarizeDto;
import edu.vrg18.cyber_chat.entity.Familiarize;
import edu.vrg18.cyber_chat.repository.FamiliarizeRepository;
import edu.vrg18.cyber_chat.service.FamiliarizeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class FamiliarizeServiceImpl implements FamiliarizeService {

    private final FamiliarizeRepository familiarizeRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public FamiliarizeServiceImpl(FamiliarizeRepository familiarizeRepository, ModelMapper modelMapper) {
        this.familiarizeRepository = familiarizeRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Optional<FamiliarizeDto> getFamiliarizeById(UUID id) {
        return familiarizeRepository.findById(id).map(f -> modelMapper.map(f, FamiliarizeDto.class));
    }

    @Override
    public FamiliarizeDto createFamiliarize(FamiliarizeDto familiarizeDto) {
        return modelMapper.map(familiarizeRepository
                .save(modelMapper.map(familiarizeDto, Familiarize.class)), FamiliarizeDto.class);
    }

    @Override
    public FamiliarizeDto updateFamiliarize(FamiliarizeDto familiarizeDto) {
        return modelMapper.map(familiarizeRepository
                .save(modelMapper.map(familiarizeDto, Familiarize.class)), FamiliarizeDto.class);
    }

    @Override
    public void deleteFamiliarize(UUID id) {
        familiarizeRepository.deleteById(id);
    }

    @Override
    public Page<FamiliarizeDto> findAllFamiliarizes(int currentPage, int pageSize) {
        return familiarizeRepository.findAll(PageRequest.of(currentPage, pageSize))
                .map(f -> modelMapper.map(f, FamiliarizeDto.class));
    }
}
