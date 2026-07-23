package ru.otus.java.spring.project.promotion.tasks;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import ru.otus.java.spring.project.promotion.domains.promotions.PromoCampaign;
import ru.otus.java.spring.project.promotion.domains.promotions.ProviderHotelData;
import ru.otus.java.spring.project.promotion.dtos.response.HotelRoomsDto;
import ru.otus.java.spring.project.promotion.repositories.promotions.ProviderHotelDataRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Component
public class PromoCampaignDataHandler {

    private final ProviderHotelDataRepository providerHotelDataRepository;

    public void writeProviderData(PromoCampaignData campaignData) {
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

    public List<ProviderHotelData> getTargetData(PromoCampaignData campaignData) {
        PromoCampaign promoCampaign = campaignData.getPromoCampaign();
        return  promoCampaign.getHotelParameters().stream()
                .map(param -> {
                    List<ProviderHotelData> providerHotelData;

                    switch (promoCampaign.getCampaignType()) {
                        case LOW_COST -> providerHotelData = providerHotelDataRepository.findWithMinPrice(promoCampaign.getId(), param.getCityName());
                        case LOW_COST_WITH_FOOD -> providerHotelData = param.getCtFoodTypes().stream().flatMap(food ->
                                providerHotelDataRepository.findWithMinPriceAndFood(promoCampaign.getId(), param.getCityName(), food.getDescription()).stream())
                                .toList();
                        default -> providerHotelData = Collections.emptyList();
                    }

                    return providerHotelData;
                }).flatMap(Collection::stream).toList();
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
