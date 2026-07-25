package ru.otus.java.spring.project.promotion.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.otus.java.spring.project.promotion.dtos.response.ProviderHotelDataDto;
import ru.otus.java.spring.project.promotion.services.promotions.ProviderHotelDataService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ProviderHotelDataRestController {

    private final ProviderHotelDataService providerHotelDataService;

    @GetMapping(value = "/api/v1/top_data", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<ProviderHotelDataDto> getTopData(@RequestParam Long campaignId) {
        return providerHotelDataService.getTop(campaignId);
    }

    @DeleteMapping("/api/v1/top_data")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByCampaignId(@RequestParam Long campaignId) {
        providerHotelDataService.deleteByCampaignId(campaignId);
    }
}
