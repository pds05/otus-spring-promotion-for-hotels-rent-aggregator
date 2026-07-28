package ru.otus.java.spring.project.promotion.dtos.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.*;
import ru.otus.java.spring.project.promotion.validators.DateRangeValid;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@DateRangeValid(before = "dateIn", after = "dateOut")
public class CampaignHotelParameterRqDto {

    public static final String DATE_IN_FIELD = "dateIn";
    public static final String DATE_OUT_FIELD = "dateOut";
    public static final String CITY_ID_FIELD = "cityId";

    @JsonSetter(nulls = Nulls.SKIP)
    private Long id;

    @JsonSetter(nulls = Nulls.SKIP)
    private Long campaignId;

    @Positive
    private Long cityId;

    @FutureOrPresent(message = DATE_IN_FIELD + " check-in date must be greater or equal to the current date")
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "UTC")
    private LocalDate dateIn;

    @Future(message = DATE_OUT_FIELD + " departure date must be greater to the current date")
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "UTC")
    private LocalDate dateOut;

    @Positive
    private Integer guests;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<Long> hotelTypeIds;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<Long> foodTypeIds;

}
