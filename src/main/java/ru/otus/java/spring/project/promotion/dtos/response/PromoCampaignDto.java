package ru.otus.java.spring.project.promotion.dtos.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PromoCampaignDto {

    private Long id;

    private String title;

    private String campaignType;

    private LocalDateTime startDate;

    private String status;

    private String result;

    private List<ProviderDto> providers;

    private List<CampaignHotelParameterDto> hotelParameters;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class ProviderDto {

        private Long id;

        private String name;

    }
}
