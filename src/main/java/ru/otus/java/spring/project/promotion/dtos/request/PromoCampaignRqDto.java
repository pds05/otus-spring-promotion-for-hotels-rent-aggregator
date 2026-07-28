package ru.otus.java.spring.project.promotion.dtos.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import ru.otus.java.spring.project.promotion.enums.PromoCampaignType;
import ru.otus.java.spring.project.promotion.validators.RequestParameterValid;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PromoCampaignRqDto {

    public static final String START_DATE_FIELD = "startDate";
    public static final String CAMPAIGN_TYPE_FIELD = "campaignType";
    public static final String TITLE_FIELD = "title";

    @JsonSetter(nulls = Nulls.SKIP)
    private Long id;

    @NotBlank(message = TITLE_FIELD + " is required")
    private String title;
    
    @RequestParameterValid(message = CAMPAIGN_TYPE_FIELD + " is not valid", source = PromoCampaignType.class)
    private String campaignType;

    @FutureOrPresent(message = START_DATE_FIELD + " check-in date must be greater or equal to the current date")
//    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "UTC", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;

    private List<CampaignHotelParameterRqDto> hotelParameters = new ArrayList<>();

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<Long> providerIds = new ArrayList<>();
}
