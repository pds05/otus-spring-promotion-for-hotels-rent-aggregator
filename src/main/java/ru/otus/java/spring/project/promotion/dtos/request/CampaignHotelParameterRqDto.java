package ru.otus.java.spring.project.promotion.dtos.request;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CampaignHotelParameterRqDto {

    private Long id;

    private String cityName;

    private LocalDate dateIn;

    private LocalDate dateOut;

    private Integer guests;

    private List<Long> hotelTypeIds;

    private List<Long> foodTypeIds;

}
