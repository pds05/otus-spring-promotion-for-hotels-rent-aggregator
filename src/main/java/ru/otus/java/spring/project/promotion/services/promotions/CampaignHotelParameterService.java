package ru.otus.java.spring.project.promotion.services.promotions;

import ru.otus.java.spring.project.promotion.dtos.request.CampaignHotelParameterRqDto;
import ru.otus.java.spring.project.promotion.dtos.response.CampaignHotelParameterDto;

import java.util.List;

public interface CampaignHotelParameterService {

    List<CampaignHotelParameterDto> getAllByCampaignId(long campaignId);

    CampaignHotelParameterDto getById(long id);

    CampaignHotelParameterDto save(CampaignHotelParameterRqDto request);

    void deleteByCampaignId(long campaignId);

    void deleteById(long id);

}
