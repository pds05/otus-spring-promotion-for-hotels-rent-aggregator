package ru.otus.java.spring.project.promotion.dtos.request;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PromoCampaignRqDto {

    private Long id;

    private String title;

    private String campaignType;

    private LocalDateTime startDate;

    private String messageGroupName;

    private List<CampaignHotelParameterRqDto> hotelParameters = new ArrayList<>();

    private List<Long> providerIds = new ArrayList<>();
}
