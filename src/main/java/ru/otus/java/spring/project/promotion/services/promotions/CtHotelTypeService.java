package ru.otus.java.spring.project.promotion.services.promotions;

import ru.otus.java.spring.project.promotion.domains.promotions.CtHotelType;

import java.util.List;

public interface CtHotelTypeService {

    CtHotelType getById(long id);

    CtHotelType getByName(String name);

    List<CtHotelType> getAll();

    CtHotelType save(CtHotelType ctHotelType);

    void delete(CtHotelType ctHotelType);
}
