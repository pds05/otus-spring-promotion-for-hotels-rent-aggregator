package ru.otus.java.spring.project.promotion.services.promotions;

import ru.otus.java.spring.project.promotion.domains.promotions.PromoCampaignResult;
import ru.otus.java.spring.project.promotion.domains.promotions.PromoCampaignStatus;
import ru.otus.java.spring.project.promotion.dtos.request.PromoCampaignRqDto;
import ru.otus.java.spring.project.promotion.dtos.response.PromoCampaignDto;

import java.util.List;

public interface PromoCampaignService {

    List<PromoCampaignDto> getAll();

    PromoCampaignDto get(long promoCampaignId);

    PromoCampaignDto save(PromoCampaignRqDto request);

    PromoCampaignDto start(long promoCampaignId);

    PromoCampaignDto stop(long promoCampaignId);

    PromoCampaignDto abort(long promoCampaignId, String reason);

    PromoCampaignDto changeStatus(long promoCampaignId, PromoCampaignStatus status, PromoCampaignResult result);

    PromoCampaignStatus getStatus(long promoCampaignId);

    PromoCampaignResult getResult(long promoCampaignId);

    void delete(long promoCampaignId);
}
