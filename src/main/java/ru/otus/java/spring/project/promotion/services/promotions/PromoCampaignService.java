package ru.otus.java.spring.project.promotion.services.promotions;

import ru.otus.java.spring.project.promotion.enums.PromoCampaignResult;
import ru.otus.java.spring.project.promotion.enums.PromoCampaignStatus;
import ru.otus.java.spring.project.promotion.dtos.request.PromoCampaignRqDto;
import ru.otus.java.spring.project.promotion.dtos.response.PromoCampaignDto;

import java.util.List;

public interface PromoCampaignService {

    List<PromoCampaignDto> getAll();

    PromoCampaignDto get(long id);

    PromoCampaignDto save(PromoCampaignRqDto request);

    PromoCampaignDto start(long id);

    PromoCampaignDto stop(long id);

    PromoCampaignDto abort(long id, String reason);

    PromoCampaignDto changeStatus(long id, PromoCampaignStatus status, PromoCampaignResult result);

    PromoCampaignStatus getStatus(long id);

    PromoCampaignResult getResult(long id);

    void deleteById(long id);
}
