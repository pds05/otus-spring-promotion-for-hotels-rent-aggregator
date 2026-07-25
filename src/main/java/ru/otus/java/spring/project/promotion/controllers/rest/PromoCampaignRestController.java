package ru.otus.java.spring.project.promotion.controllers.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.otus.java.spring.project.promotion.dtos.request.PromoCampaignRqDto;
import ru.otus.java.spring.project.promotion.dtos.response.PromoCampaignDto;
import ru.otus.java.spring.project.promotion.services.promotions.PromoCampaignService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class PromoCampaignRestController {

    private final PromoCampaignService promoCampaignService;

    @GetMapping(value = "/api/v1/promo_campaign", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<PromoCampaignDto> getAllPromoCampaigns() {
        return promoCampaignService.getAll();
    }

    @GetMapping(value = "/api/v1/promo_campaign/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public PromoCampaignDto getPromoCampaign(@PathVariable Long id) {
        return promoCampaignService.get(id);
    }

    @DeleteMapping("/api/v1/promo_campaign/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePromoCampaign(@PathVariable Long id) {
        promoCampaignService.delete(id);
    }

    @PostMapping(value = "/api/v1/promo_campaign",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public PromoCampaignDto savePromoCampaign(@Valid @RequestBody PromoCampaignRqDto promoCampaignRequest) {
        return promoCampaignService.save(promoCampaignRequest);
    }

    @PutMapping(value = "/api/v1/promo_campaign/{id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public PromoCampaignDto updatePromoCampaign(@Valid @RequestBody PromoCampaignRqDto promoCampaignRequest,
                                                @PathVariable("id") Long id) {
        promoCampaignRequest.setId(id);
        return promoCampaignService.save(promoCampaignRequest);
    }

    @PutMapping(value = "/api/v1/promo_campaign/start/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PromoCampaignDto startPromoCampaign(@PathVariable("id") Long id) {
        return promoCampaignService.start(id);
    }

    @PutMapping(value = "/api/v1/promo_campaign/stop/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PromoCampaignDto stopPromoCampaign(@PathVariable("id") Long id) {
        return promoCampaignService.stop(id);
    }

}
