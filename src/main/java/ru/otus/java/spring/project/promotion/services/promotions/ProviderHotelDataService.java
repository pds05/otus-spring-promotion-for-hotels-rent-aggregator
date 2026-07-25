package ru.otus.java.spring.project.promotion.services.promotions;

import ru.otus.java.spring.project.promotion.dtos.response.ProviderHotelDataDto;
import ru.otus.java.spring.project.promotion.tasks.PromoCampaignData;

import java.util.List;

public interface ProviderHotelDataService {

    List<ProviderHotelDataDto> getTop(long campaignId);

    void save(PromoCampaignData campaignData);

    void deleteByCampaignId(long campaignId);

}
