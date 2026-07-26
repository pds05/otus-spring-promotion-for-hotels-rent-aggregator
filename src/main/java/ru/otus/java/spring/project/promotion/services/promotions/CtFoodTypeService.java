package ru.otus.java.spring.project.promotion.services.promotions;

import ru.otus.java.spring.project.promotion.dtos.response.FoodTypeDto;

import java.util.List;


public interface CtFoodTypeService {

    FoodTypeDto getById(long id);

    FoodTypeDto getByName(String name);

    List<FoodTypeDto> getAll();

    FoodTypeDto save(String name, String description);

    FoodTypeDto update(long id, String name, String description);

    void deleteById(long id);

}
