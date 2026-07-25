package ru.otus.java.spring.project.promotion.dtos.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PromoCampaignDto {

    private Long id;

    private String title;

    private String campaignType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "UTC", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;

    private String status;

    private String result;

    private String details;

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
