package ru.otus.java.spring.project.promotion.services.promotions;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.java.spring.project.promotion.services.cache.CitiesCache;
import ru.otus.java.spring.project.promotion.domains.promotions.CtCity;
import ru.otus.java.spring.project.promotion.dtos.response.CityDto;
import ru.otus.java.spring.project.promotion.exceptions.ResourceNotFoundException;
import ru.otus.java.spring.project.promotion.repositories.promotions.CtCitiesRepository;

import java.util.List;

@RequiredArgsConstructor
@Service("CtCitiesService")
public class CtCitiesServiceImpl implements CtCitiesService {

    private final CtCitiesRepository ctCitiesRepository;

    private final ModelMapper modelMapper;

    private final CitiesCache citiesCache;

    @Transactional(readOnly = true)
    @Override
    public CityDto getById(long id) {
        CtCity ctCity;
        if (citiesCache.get(id) == null) {
            ctCity = ctCitiesRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("City with id " + id + " not found"));
            citiesCache.put(ctCity);
        }
        return modelMapper.map(citiesCache.get(id), CityDto.class);
    }

    @Transactional(readOnly = true)
    @Override
    public CityDto getByTitle(String title) {
        CtCity ctCity = citiesCache.getAll().stream().filter(city -> city.getTitle().equals(title)).findFirst().orElse(null);
        if (ctCity == null) {
            ctCity = ctCitiesRepository.findByTitle(title).orElseThrow(() -> new ResourceNotFoundException("City with title " + title + " not found"));
            citiesCache.put(ctCity);
        }
        return modelMapper.map(ctCity, CityDto.class);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CityDto> getAll() {
        if (citiesCache.isEmpty()) {
            List<CtCity> cities = ctCitiesRepository.findAll();
            if (cities.isEmpty()) {
                throw new ResourceNotFoundException("Cities not found");
            }
            citiesCache.putAll(cities);
        }
        return modelMapper.map(citiesCache.getAll(), new TypeToken<List<CtCity>>() {
        }.getType());
    }

    @Transactional
    @Override
    public CityDto save(String title) {
        CtCity ctCity = new CtCity();
        ctCity.setTitle(title);
        ctCity = ctCitiesRepository.save(ctCity);
        citiesCache.put(ctCity);
        return modelMapper.map(ctCity, CityDto.class);
    }

    @Transactional
    @Override
    public CityDto update(long id, String title) {
        CtCity ctCity = citiesCache.get(id);
        if (ctCity == null) {
            ctCity = ctCitiesRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("City with id " + id + " not found"));
        }
        ctCity.setTitle(title);

        ctCity = ctCitiesRepository.save(ctCity);
        citiesCache.put(ctCity);

        return modelMapper.map(ctCity, CityDto.class);
    }

    @Transactional
    @Override
    public void deleteById(long id) {
        ctCitiesRepository.deleteById(id);
    }
}
