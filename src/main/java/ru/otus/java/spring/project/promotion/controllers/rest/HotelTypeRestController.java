package ru.otus.java.spring.project.promotion.controllers.rest;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.otus.java.spring.project.promotion.dtos.response.HotelTypeDto;
import ru.otus.java.spring.project.promotion.services.cache.HotelTypeCache;
import ru.otus.java.spring.project.promotion.services.promotions.CtHotelTypeService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class HotelTypeRestController {

    private final HotelTypeCache hotelTypeCache;

    private final CtHotelTypeService ctHotelTypeService;

    private final ModelMapper modelMapper;

    @GetMapping(value = "/api/v1/hotel_type", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    private List<HotelTypeDto> getHotelTypes() {
        return modelMapper.map(hotelTypeCache.getAll(), new TypeToken<List<HotelTypeDto>>() {
        }.getType());
    }

    @GetMapping(value = "/api/v1/hotel_type/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    private HotelTypeDto getHotelTypeById(@PathVariable Long id) {
        return modelMapper.map(hotelTypeCache.get(id), HotelTypeDto.class);
    }

    @PostMapping(value = "/api/v1/hotel_type",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public HotelTypeDto saveHotelType(@RequestParam @NotBlank String name,
                                      @RequestParam @NotBlank String description) {
        return ctHotelTypeService.save(name, description);
    }

    @PutMapping(value = "/api/v1/hotel_type/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public HotelTypeDto updateHotelType(@PathVariable Long id,
                                        @RequestParam @NotBlank String name,
                                        @RequestParam @NotBlank String description) {
        return ctHotelTypeService.update(id, name, description);
    }

    @DeleteMapping("/api/v1/hotel_type/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteHotelType(@PathVariable Long id) {
        ctHotelTypeService.deleteById(id);
    }
}
