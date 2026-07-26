package ru.otus.java.spring.project.promotion.controllers.rest;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.otus.java.spring.project.promotion.dtos.response.FoodTypeDto;
import ru.otus.java.spring.project.promotion.services.cache.FoodTypeCache;
import ru.otus.java.spring.project.promotion.services.promotions.CtFoodTypeService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class FoodTypeRestController {

    private final FoodTypeCache foodTypeCache;

    private final CtFoodTypeService ctFoodTypeService;

    private final ModelMapper modelMapper;

    @GetMapping(value = "/api/v1/food_type", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    private List<FoodTypeDto> getAllFoodTypes() {
        return modelMapper.map(foodTypeCache.getAll(), new TypeToken<List<FoodTypeDto>>() {
        }.getType());
    }

    @GetMapping(value = "/api/v1/food_type/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    private FoodTypeDto getFoodTypeById(@PathVariable Long id) {
        return modelMapper.map(foodTypeCache.get(id), FoodTypeDto.class);
    }

    @PostMapping(value = "/api/v1/food_type",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public FoodTypeDto saveFoodType(@RequestParam @NotBlank String name,
                                @RequestParam @NotBlank String description) {
        return ctFoodTypeService.save(name, description);
    }

    @PutMapping(value = "/api/v1/food_type/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public FoodTypeDto updateFoodType(@PathVariable Long id,
                                  @RequestParam @NotBlank String name,
                                  @RequestParam @NotBlank String description) {
        return ctFoodTypeService.update(id, name, description);
    }

    @DeleteMapping("/api/v1/food_type/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFoodType(@PathVariable Long id) {
        ctFoodTypeService.deleteById(id);
    }
}
