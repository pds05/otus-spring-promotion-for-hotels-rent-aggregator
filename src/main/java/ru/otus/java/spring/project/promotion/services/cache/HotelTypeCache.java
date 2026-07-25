package ru.otus.java.spring.project.promotion.services.cache;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.java.spring.project.promotion.domains.promotions.CtHotelType;
import ru.otus.java.spring.project.promotion.repositories.promotions.CtHotelTypeRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service("hotelTypeCache")
public class HotelTypeCache implements ModelCache<CtHotelType> {

    private final CtHotelTypeRepository ctHotelTypeRepository;

    private static final Map<Long, CtHotelType> CACHE = new HashMap<>();

    @PostConstruct
    private void init(){
        CACHE.putAll(ctHotelTypeRepository.findAll().stream().collect(Collectors.toMap(CtHotelType::getId, c -> c)));
    }

    @Override
    public CtHotelType get(long id) {
        return CACHE.get(id);
    }

    @Override
    public List<CtHotelType> getByIds(Collection<Long> ids) {
        return CACHE.entrySet().stream().filter(entry -> ids.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public void put(CtHotelType hotelType) {
        CACHE.put(hotelType.getId(), hotelType);
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
    public void putAll(Collection<CtHotelType> models) {
        CACHE.putAll(models.stream().collect(Collectors.toMap(CtHotelType::getId, c -> c)));
    }

    @Override
    public List<CtHotelType> getAll() {
        return CACHE.values().stream().toList();
    }
}
