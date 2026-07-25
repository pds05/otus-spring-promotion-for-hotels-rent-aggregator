package ru.otus.java.spring.project.promotion.services.promotions;

import ru.otus.java.spring.project.promotion.dtos.response.CityDto;

import java.util.List;

public interface CtCitiesService {

    CityDto getById(long id);

    CityDto getByTitle(String title);

    List<CityDto> getAll();

    CityDto save(String title);

    CityDto update(long id, String title);

    void delete(long id);

}
