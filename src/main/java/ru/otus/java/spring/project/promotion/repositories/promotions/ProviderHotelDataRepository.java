package ru.otus.java.spring.project.promotion.repositories.promotions;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.otus.java.spring.project.promotion.domains.promotions.ProviderHotelData;

import java.util.List;

public interface ProviderHotelDataRepository extends JpaRepository<ProviderHotelData, Long> {

    void deleteById(Long id);

    @EntityGraph(attributePaths = "promoCampaign")
    List<ProviderHotelData> findByProviderIdAndPromoCampaignId(Long hotelId, Long campaignId);

    List<ProviderHotelData> findByPromoCampaignId(Long campaignId);

    @EntityGraph(attributePaths = "promoCampaign")
    @Query(value = "with min_price as (" +
            "select min(price) m_price from provider_hotel_data " +
            "where campaign_id = :campaignId and city_name = :city) " +
            "select * from provider_hotel_data " +
            "where campaign_id = :campaignId and city_name = :city  " +
            "and price = (select * from min_price)",  nativeQuery = true)
    List<ProviderHotelData> findWithMinPrice(Long campaignId, String city);

//    @EntityGraph(attributePaths = "promoCampaign")
//    @Query(value = "with min_price as ( " +
//            "select min(price) m_price from provider_hotel_data " +
//            "where campaign_id = :campaignId and city_name = :city and food like :foodRegex) " +
//            "select * from provider_hotel_data " +
//            "where campaign_id = :campaignId and city_name = :city and food like :foodRegex " +
//            "and price = (select * from min_price)",  nativeQuery = true)
//    @EntityGraph(attributePaths = "promoCampaign")
    @Query(value = "with min_price as ( " +
            "select min(price) m_price from provider_hotel_data " +
            "where campaign_id = :campaignId and city_name = :city and food like :foodRegex) " +
            "select phd.* from provider_hotel_data phd " +
//            "inner join promo_campaigns pc on phd.campaign_id = pc.id " +
//            "inner join campaign_hotel_parameters chp on chp.campaign_id = pc.id " +
//            "inner join campaign_hotel_parameters_ct_hotel_type_rel ht_rel on ht_rel.campaign_hotel_parameter_id = chp.id " +
//            "inner join ct_hotel_types ht on ht.id = ht_rel.ct_hotel_type_id " +
//            "inner join campaign_hotel_parameters_ct_food_type_rel food_rel on food_rel.campaign_hotel_parameter_id = chp.id " +
//            "inner join ct_food_types ft on ft.id = food_rel.ct_food_type_id " +
            "where phd.campaign_id = :campaignId and phd.city_name = :city and phd.food like :foodRegex " +
            "and phd.price = (select * from min_price)",  nativeQuery = true)
    List<ProviderHotelData> findWithMinPriceAndFood(Long campaignId, String city, String foodRegex);

    void deleteByPromoCampaignId(Long campaignId);
}
