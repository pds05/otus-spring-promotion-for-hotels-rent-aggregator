package ru.otus.java.spring.project.promotion.controllers.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.otus.java.spring.project.promotion.dtos.request.CampaignHotelParameterRqDto;
import ru.otus.java.spring.project.promotion.dtos.response.CampaignHotelParameterDto;
import ru.otus.java.spring.project.promotion.services.promotions.CampaignHotelParameterService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class CampaignHotelParameterRestController {

    private final CampaignHotelParameterService campaignHotelParameterService;

    @GetMapping(value = "/api/v1/campaign_hotel_parameter", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<CampaignHotelParameterDto> getCampaignHotelParameters(@RequestParam Long campaignId) {
        return campaignHotelParameterService.getAllByCampaignId(campaignId);
    }

    @GetMapping(value = "/api/v1/campaign_hotel_parameter/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public CampaignHotelParameterDto getCampaignHotelParameter(@PathVariable Long id) {
        return campaignHotelParameterService.getById(id);
    }

    @PostMapping(value = "/api/v1/campaign_hotel_parameter",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public CampaignHotelParameterDto saveCampaignHotelParameter(
            @Valid @RequestBody CampaignHotelParameterRqDto campaignHotelParameter) {
        return campaignHotelParameterService.save(campaignHotelParameter);
    }

    @PostMapping(value = "/api/v1/campaign_hotel_parameter/{id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public CampaignHotelParameterDto updateCampaignHotelParameter(
            @PathVariable Long id,
            @Valid  @RequestBody CampaignHotelParameterRqDto campaignHotelParameter) {
        campaignHotelParameter.setId(id);
        return campaignHotelParameterService.save(campaignHotelParameter);
    }

    @DeleteMapping("/api/v1/campaign_hotel_parameter/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCampaignHotelParameter(@PathVariable Long id) {
        campaignHotelParameterService.deleteById(id);
    }

    @DeleteMapping("/api/v1/campaign_hotel_parameter")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCampaignHotelParameterByCampaignId(@RequestParam @Positive Long campaignId) {
        campaignHotelParameterService.getAllByCampaignId(campaignId);
    }
}
