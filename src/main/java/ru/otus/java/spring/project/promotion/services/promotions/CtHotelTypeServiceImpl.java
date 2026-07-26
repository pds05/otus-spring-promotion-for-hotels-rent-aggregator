package ru.otus.java.spring.project.promotion.services.promotions;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.java.spring.project.promotion.services.cache.HotelTypeCache;
import ru.otus.java.spring.project.promotion.domains.promotions.CtHotelType;
import ru.otus.java.spring.project.promotion.dtos.response.HotelTypeDto;
import ru.otus.java.spring.project.promotion.exceptions.ResourceNotFoundException;
import ru.otus.java.spring.project.promotion.repositories.promotions.CtHotelTypeRepository;

import java.util.List;

@RequiredArgsConstructor
@Service("ctHotelTypeService")
public class CtHotelTypeServiceImpl implements CtHotelTypeService {

    private final CtHotelTypeRepository ctHotelTypeRepository;

    private final ModelMapper modelMapper;

    private final HotelTypeCache hotelTypeCache;

    @Transactional(readOnly = true)
    @Override
    public HotelTypeDto getById(long id) {
        CtHotelType ctHotelType;
        if (hotelTypeCache.get(id) == null) {
            ctHotelType = ctHotelTypeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Hotel type with id " + id + " not found"));
            hotelTypeCache.put(ctHotelType);
        }
        return modelMapper.map(hotelTypeCache.get(id), HotelTypeDto.class);
    }

    @Transactional(readOnly = true)
    @Override
    public HotelTypeDto getByName(String name) {
        CtHotelType ctHotelType = hotelTypeCache.getAll().stream().filter(hotelType -> hotelType.getName().equals(name)).findFirst().orElse(null);
        if (ctHotelType == null) {
            ctHotelType = ctHotelTypeRepository.findByName(name).orElseThrow(() -> new ResourceNotFoundException("Hotel type with name " + name + " not found"));
            hotelTypeCache.put(ctHotelType);
        }
        return modelMapper.map(ctHotelType, HotelTypeDto.class);
    }

    @Transactional(readOnly = true)
    @Override
    public List<HotelTypeDto> getAll() {
        if (hotelTypeCache.isEmpty()) {
            List<CtHotelType> hotelTypes = ctHotelTypeRepository.findAll();
            if (hotelTypes.isEmpty()) {
                throw new ResourceNotFoundException("Hotel types not found");
            }
            hotelTypeCache.putAll(hotelTypes);
        }

        return modelMapper.map(hotelTypeCache.getAll(), new TypeReference<List<HotelTypeDto>>() {
        }.getType());
    }

    @Transactional
    @Override
    public HotelTypeDto save(String name, String description) {
        CtHotelType ctHotelType = new CtHotelType();
        ctHotelType.setName(name);
        ctHotelType.setDescription(description);

        ctHotelType = ctHotelTypeRepository.save(ctHotelType);

        hotelTypeCache.put(ctHotelType);

        return modelMapper.map(ctHotelType, HotelTypeDto.class);
    }

    @Transactional
    @Override
    public HotelTypeDto update(long id, String name, String description) {
        CtHotelType hotelType = hotelTypeCache.get(id);
        if (hotelType == null) {
            hotelType = ctHotelTypeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Hotel type with id " + id + " not found"));
        }
        hotelType.setName(name);
        hotelType.setDescription(description);

        hotelType = ctHotelTypeRepository.save(hotelType);

        hotelTypeCache.put(hotelType);

        return modelMapper.map(hotelType, HotelTypeDto.class);
    }

    @Transactional
    @Override
    public void deleteById(long id) {
        ctHotelTypeRepository.deleteById(id);
    }
}
