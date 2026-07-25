package ru.otus.java.spring.project.promotion.dtos.request;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProviderRequestDto {

    private String city;

    private LocalDate dateIn;

    private LocalDate dateOut;

    private Integer guests;

    private List<String> hotelTypes;

    private List<String> foods;

}
