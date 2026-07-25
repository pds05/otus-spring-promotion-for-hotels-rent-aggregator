package ru.otus.java.spring.project.promotion.services.cache;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.java.spring.project.promotion.domains.promotions.CtCity;
import ru.otus.java.spring.project.promotion.repositories.promotions.CtCitiesRepository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service("citiesCache")
public class CitiesCache implements ModelCache<CtCity> {

    private final CtCitiesRepository ctCitiesRepository;

    private static final Map<Long, CtCity> CACHE = new HashMap<>();

    @PostConstruct
    private void init(){
        CACHE.putAll(ctCitiesRepository.findAll().stream().collect(Collectors.toMap(CtCity::getId, c -> c)));
    }

    @Override
    public CtCity get(long id) {
        return CACHE.get(id);
    }

    @Override
    public List<CtCity> getByIds(Collection<Long> ids) {
        return CACHE.entrySet().stream().filter(entry -> ids.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public void put(CtCity ctCity) {
        if (CACHE.containsKey(ctCity.getId())) {
            CACHE.replace(ctCity.getId(), ctCity);
        } else {
            CACHE.put(ctCity.getId(), ctCity);
        }
    }

    @Override
    public void remove(long id) {
        CACHE.remove(id);
    }

    @Override
    public int size() {
        return CACHE.size();
    }

    @Override
    public boolean isEmpty() {
        return CACHE.isEmpty();
    }

    @Override
    public void putAll(Collection<CtCity> models) {
        CACHE.putAll(models.stream().collect(Collectors.toMap(CtCity::getId, c -> c)));
    }

    @Override
    public List<CtCity> getAll() {
        return CACHE.values().stream().toList();
    }
}
