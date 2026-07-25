package ru.otus.java.spring.project.promotion.services.promotions;

import ru.otus.java.spring.project.promotion.dtos.response.HotelTypeDto;

import java.util.List;

public interface CtHotelTypeService {

    HotelTypeDto getById(long id);

    HotelTypeDto getByName(String name);

    List<HotelTypeDto> getAll();

    HotelTypeDto save(String name, String description);

    HotelTypeDto update(long id, String name, String description);

    void delete(long id);
}
