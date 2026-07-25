package ru.otus.java.spring.project.promotion.services.promotions;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.java.spring.project.promotion.domains.promotions.CtFoodType;
import ru.otus.java.spring.project.promotion.domains.promotions.PromoCampaign;
import ru.otus.java.spring.project.promotion.domains.promotions.ProviderHotelData;
import ru.otus.java.spring.project.promotion.dtos.response.HotelRoomsDto;
import ru.otus.java.spring.project.promotion.dtos.response.ProviderHotelDataDto;
import ru.otus.java.spring.project.promotion.repositories.promotions.ProviderHotelDataRepository;
import ru.otus.java.spring.project.promotion.tasks.PromoCampaignData;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Service("providerHotelDataService")
public class ProviderHotelDataServiceImpl implements ProviderHotelDataService {

    private final ProviderHotelDataRepository providerHotelDataRepository;

    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    @Override
    public List<ProviderHotelDataDto> getTop(long campaignId) {
        return modelMapper.map(providerHotelDataRepository.findByPromoCampaignIdAndIsTopIsTrue(campaignId),
                new TypeToken<List<ProviderHotelDataDto>>() {}.getType());
    }

    @Transactional
    public void save(PromoCampaignData campaignData) {
        var hotelDataList = campaignData.getProviderDataMultiMap().values().stream().flatMap(Collection::stream)
                .map(providerData -> providerData.getResponseList().stream().<ProviderFlatResponse>mapMulti((hotel, consumer) -> {
                    for (HotelRoomsDto.HotelRoomDto room : hotel.getRooms()) {
                                for (HotelRoomsDto.HotelRoomRateDto rate : room.getRates()) {
                                    consumer.accept(new ProviderFlatResponse(hotel, room, rate));
                                }
                            }
                        }).map(record -> ProviderHotelData.builder()
                                .promoCampaign(campaignData.getPromoCampaign())
                                .cityName(providerData.getRequest().getCity())
                                .providerId(providerData.getProviderId())
                                .hotelId(record.getHotel().getId())
                                .hotelName(record.getHotel().getTitle())
                                .hotelRoomId(record.getHotelRoom().getId())
                                .hotelRoomName(record.getHotelRoom().getTitle())
                                .hotelRoomRateId(record.getHotelRoomRate().getId())
                                .hotelRoomRateName(record.getHotelRoomRate().getTitle())
                                .food(record.getHotelRoomRate().getFood())
                                .price(record.getHotelRoomRate().getPrice())
                                .maxGuests(record.getHotelRoom().getMaxGuests())
                                .dateIn(LocalDate.now().plusDays(1))
                                .dateOut(LocalDate.now().plusDays(2))
                                .build())
                        .toList())
                .flatMap(Collection::stream).toList();
        providerHotelDataRepository.saveAll(hotelDataList);
    }

    @Transactional
    @Override
    public void deleteByCampaignId(long campaignId) {
        providerHotelDataRepository.deleteByPromoCampaignId(campaignId);
    }

    @Transactional
    public List<ProviderHotelData> parseTop(PromoCampaign promoCampaign) {
        List<ProviderHotelData> topList = promoCampaign.getHotelParameters().stream()
                .map(param -> {
                    List<ProviderHotelData> providerHotelData;

                    switch (promoCampaign.getCampaignType()) {
                        case LOW_COST -> providerHotelData = providerHotelDataRepository.findWithMinPrice(promoCampaign.getId(), param.getCity().getTitle());
                        case LOW_COST_WITH_FOOD -> providerHotelData = providerHotelDataRepository.findWithMinPriceAndFood(promoCampaign.getId(),
                                param.getCity().getTitle(),
                                param.getCtFoodTypes().stream().map(CtFoodType::getDescription).toList());
                        default -> providerHotelData = Collections.emptyList();
                    }

                    return providerHotelData;
                }).flatMap(Collection::stream)
                .peek(data -> data.setIsTop(true))
                .toList();
        return providerHotelDataRepository.saveAll(topList);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    static class ProviderFlatResponse {

        private HotelRoomsDto hotel;

        private HotelRoomsDto.HotelRoomDto hotelRoom;

        private HotelRoomsDto.HotelRoomRateDto hotelRoomRate;

    }
}
