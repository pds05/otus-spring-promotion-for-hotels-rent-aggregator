package ru.otus.java.spring.project.promotion.controllers.rest;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.otus.java.spring.project.promotion.dtos.response.CityDto;
import ru.otus.java.spring.project.promotion.services.cache.CitiesCache;
import ru.otus.java.spring.project.promotion.services.promotions.CtCitiesService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class CityRestController {

    private final CitiesCache citiesCache;

    private final CtCitiesService ctCitiesService;

    private final ModelMapper modelMapper;


    @GetMapping(value = "/api/v1/city", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<CityDto> getCities() {
        return modelMapper.map(citiesCache.getAll(), new TypeToken<List<CityDto>>(){}.getType());
    }

    @GetMapping(value = "/api/v1/city/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public CityDto getCityById(@PathVariable Long id) {
        return modelMapper.map(citiesCache.get(id), CityDto.class);
    }

    @PostMapping(value = "/api/v1/city",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public CityDto saveCity(@RequestParam @NotBlank String title) {
        return ctCitiesService.save(title);
    }

    @PutMapping(value = "/api/v1/city/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public CityDto updateCity(@PathVariable Long id,
                              @RequestParam @NotBlank String title) {
        return ctCitiesService.update(id, title);
    }

    @DeleteMapping("/api/v1/city/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCity(@PathVariable Long id) {
        ctCitiesService.delete(id);
    }
}
