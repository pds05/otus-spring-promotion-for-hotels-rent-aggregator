package ru.otus.java.spring.project.promotion.controllers.rest;

import jakarta.validation.constraints.Positive;
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

    @GetMapping("/api/v1/promo_campaign")
    @ResponseStatus(HttpStatus.OK)
    public List<PromoCampaignDto> getAllPromoCampaigns() {
        return promoCampaignService.getAll();
    }

    @GetMapping("/api/v1/promo_campaign/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PromoCampaignDto getPromoCampaign(@Positive @PathVariable Long id) {
        return promoCampaignService.get(id);
    }

    @DeleteMapping("/api/v1/promo_campaign/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePromoCampaign(@Positive @PathVariable Long id) {
        promoCampaignService.delete(id);
    }

    @PostMapping(value = "/api/v1/promo_campaign",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public PromoCampaignDto savePromoCampaign(@RequestBody PromoCampaignRqDto promoCampaignRequest) {
        return promoCampaignService.save(promoCampaignRequest);
    }

//    @PutMapping(value = "/api/v1/promo_campaign/{id}",
//            consumes = {MediaType.APPLICATION_JSON_VALUE},
//            produces = {MediaType.APPLICATION_JSON_VALUE})
//    public PromoCampaignDto updatePromoCampaign(@RequestBody PromoCampaignRqDto promoCampaignRequest,
//                                                @PathVariable("id") Long id) {
//        return promoCampaignService.
//
//    }

}
