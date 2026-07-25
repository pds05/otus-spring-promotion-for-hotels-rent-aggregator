package ru.otus.java.spring.project.promotion.services.promotions;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import ru.otus.java.spring.project.promotion.services.cache.FoodTypeCache;
import ru.otus.java.spring.project.promotion.domains.promotions.CtFoodType;
import ru.otus.java.spring.project.promotion.dtos.response.FoodTypeDto;
import ru.otus.java.spring.project.promotion.exceptions.ResourceNotFoundException;
import ru.otus.java.spring.project.promotion.repositories.promotions.CtFoodTypeRepository;

import java.util.List;

@RequiredArgsConstructor
@Service("CtFoodTypeService")
public class CtFoodTypeServiceImpl implements CtFoodTypeService {

    private final CtFoodTypeRepository ctFoodTypeRepository;

    private final ModelMapper modelMapper;

    private final FoodTypeCache foodTypeCache;

    @Override
    public FoodTypeDto getById(long id) {
        CtFoodType ctFoodType;
        if (foodTypeCache.get(id) == null) {
            ctFoodType = ctFoodTypeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("CtHotelFoodType with id " + id + " not found"));
            foodTypeCache.put(ctFoodType);
        }
        return modelMapper.map(foodTypeCache.get(id), FoodTypeDto.class);
    }

    @Override
    public FoodTypeDto getByName(String name) {
        CtFoodType ctFoodType = foodTypeCache.getAll().stream().filter(food -> food.getName().equals(name)).findFirst().orElse(null);
        if (ctFoodType == null) {
            ctFoodType = ctFoodTypeRepository.findByName(name).orElseThrow(() -> new ResourceNotFoundException("CtHotelFoodType with name " + name + " not found"));
            foodTypeCache.put(ctFoodType);
        }
        return modelMapper.map(ctFoodType, FoodTypeDto.class);
    }

    @Override
    public List<FoodTypeDto> getAll() {
        if (foodTypeCache.isEmpty()) {
            List<CtFoodType> foodTypes = ctFoodTypeRepository.findAll();
            if (foodTypes.isEmpty()) {
                throw new ResourceNotFoundException("Foods not found");
            }
            foodTypeCache.putAll(foodTypes);
        }

        return modelMapper.map(foodTypeCache.getAll(), new TypeToken<List<CtFoodType>>() {
        }.getType());
    }

    @Override
    public FoodTypeDto save(String name, String description) {
        CtFoodType ctFoodType = new CtFoodType();
        ctFoodType.setName(name);
        ctFoodType.setDescription(description);

        ctFoodType = ctFoodTypeRepository.save(ctFoodType);

        foodTypeCache.put(ctFoodType);

        return modelMapper.map(ctFoodType, FoodTypeDto.class);
    }

    @Override
    public FoodTypeDto update(long id, String name, String description) {
        CtFoodType foodType = foodTypeCache.get(id);
        if (foodType == null) {
            foodType = ctFoodTypeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Food type with id " + id + " not found"));
        }
        foodType.setName(name);
        foodType.setDescription(description);

        foodType = ctFoodTypeRepository.save(foodType);

        foodTypeCache.put(foodType);

        return modelMapper.map(foodType, FoodTypeDto.class);
    }

    @Override
    public void delete(long id) {
        ctFoodTypeRepository.deleteById(id);
    }
}
