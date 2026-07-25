package ru.otus.java.spring.project.promotion.services.promotions;

import ru.otus.java.spring.project.promotion.dtos.request.CampaignHotelParameterRqDto;
import ru.otus.java.spring.project.promotion.dtos.response.CampaignHotelParameterDto;

import java.util.List;

public interface CampaignHotelParameterService {

    List<CampaignHotelParameterDto> getAllByCampaignId(Long campaignId);

    CampaignHotelParameterDto getById(Long id);

    CampaignHotelParameterDto save(CampaignHotelParameterRqDto request);

    void deleteByCampaignId(Long campaignId);

    void deleteById(Long id);

}
