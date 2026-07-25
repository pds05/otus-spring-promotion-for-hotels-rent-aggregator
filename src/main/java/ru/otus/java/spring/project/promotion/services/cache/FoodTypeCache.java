package ru.otus.java.spring.project.promotion.services.cache;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.java.spring.project.promotion.domains.promotions.CtFoodType;
import ru.otus.java.spring.project.promotion.repositories.promotions.CtFoodTypeRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service("foodTypeCache")
public class FoodTypeCache implements ModelCache<CtFoodType> {

    private final CtFoodTypeRepository ctFoodTypeRepository;

    private static final Map<Long, CtFoodType> CACHE = new HashMap<>();

    @PostConstruct
    private void init(){
        CACHE.putAll(ctFoodTypeRepository.findAll().stream().collect(Collectors.toMap(CtFoodType::getId, c -> c)));
    }


    @Override
    public CtFoodType get(long id) {
        return CACHE.get(id);
    }

    @Override
    public List<CtFoodType> getByIds(Collection<Long> ids) {
        return CACHE.entrySet().stream().filter(entry -> ids.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public void put(CtFoodType ctFoodType) {
        if (CACHE.containsKey(ctFoodType.getId())) {
            CACHE.replace(ctFoodType.getId(), ctFoodType);
        } else {
            CACHE.put(ctFoodType.getId(), ctFoodType);
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
    public void putAll(Collection<CtFoodType> models) {
        CACHE.putAll(models.stream().collect(Collectors.toMap(CtFoodType::getId, c -> c)));
    }

    @Override
    public List<CtFoodType> getAll() {
        return CACHE.values().stream().toList();
    }
}
