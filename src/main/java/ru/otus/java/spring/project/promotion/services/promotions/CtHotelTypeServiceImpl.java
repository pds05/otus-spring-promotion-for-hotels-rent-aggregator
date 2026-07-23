package ru.otus.java.spring.project.promotion.services.promotions;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.java.spring.project.promotion.domains.promotions.CtHotelType;
import ru.otus.java.spring.project.promotion.repositories.promotions.CtHotelTypeRepository;

import java.util.List;

@RequiredArgsConstructor
@Service("ctHotelTypeService")
public class CtHotelTypeServiceImpl implements CtHotelTypeService {

    private final CtHotelTypeRepository ctHotelTypeRepository;

    @Override
    public CtHotelType getById(long id) {
        return ctHotelTypeRepository.findById(id).orElse(null);
    }

    @Override
    public CtHotelType getByName(String name) {
        return ctHotelTypeRepository.findByNameOrDescriptionContainingIgnoreCase(name, name).orElse(null);
    }

    @Override
    public List<CtHotelType> getAll() {
        return ctHotelTypeRepository.findAll();
    }

    @Override
    public CtHotelType save(CtHotelType ctHotelType) {
        return ctHotelTypeRepository.save(ctHotelType);
    }

    @Override
    public void delete(CtHotelType ctHotelType) {
        ctHotelTypeRepository.delete(ctHotelType);
    }
}
