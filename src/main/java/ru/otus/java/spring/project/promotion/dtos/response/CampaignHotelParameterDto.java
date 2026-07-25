package ru.otus.java.spring.project.promotion.dtos.response;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class CampaignHotelParameterDto {

    private Long id;

    private String cityName;

    private LocalDate dateIn;

    private LocalDate dateOut;

    private Integer guests;

    private List<HotelTypeDto> hotelTypes;

    private List<FoodTypeDto> foodTypes;

}
