package ru.otus.java.spring.project.promotion.dtos.response;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CampaignHotelParameterDto {

    private Long id;

    private String cityName;

    private LocalDate dateIn;

    private LocalDate dateOut;

    private Integer guests;

    private List<HotelTypeDto> hotelTypes;

    private List<FoodTypeDto> foodTypes;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class HotelTypeDto {

        private Long id;

        private String name;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class FoodTypeDto {

        private Long id;

        private String name;

    }
}
