package ru.otus.java.spring.project.promotion.repositories.promotions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.otus.java.spring.project.promotion.domains.promotions.ProviderHotelData;

import java.util.List;

public interface ProviderHotelDataRepository extends JpaRepository<ProviderHotelData, Long> {

    List<ProviderHotelData> findByPromoCampaignId(Long campaignId);

    List<ProviderHotelData> findByPromoCampaignIdAndIsTopIsTrue(Long campaignId);

    @Query(value = "with min_price as (" +
            "select min(price) m_price from provider_hotel_data " +
            "where campaign_id = :campaignId and city_name = :city) " +
            "select * from provider_hotel_data " +
            "where campaign_id = :campaignId and city_name = :city  " +
            "and price = (select * from min_price)", nativeQuery = true)
    List<ProviderHotelData> findWithMinPrice(Long campaignId, String city);

    @Query(value = "with min_price as ( " +
            "select min(price) m_price from provider_hotel_data " +
            "where campaign_id = :campaignId and city_name = :city and food in :foodTypes) " +
            "select phd.* from provider_hotel_data phd " +
            "where phd.campaign_id = :campaignId and phd.city_name = :city and phd.food in :foodTypes " +
            "and phd.price = (select * from min_price)", nativeQuery = true)
    List<ProviderHotelData> findWithMinPriceAndFood(Long campaignId, String city, List<String> foodTypes);

    void deleteByPromoCampaignId(Long campaignId);

}
