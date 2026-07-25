package ru.otus.java.spring.project.promotion.repositories.promotions;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.otus.java.spring.project.promotion.domains.promotions.CampaignHotelParameter;

import java.util.List;
import java.util.Optional;

public interface CampaignHotelParameterRepository extends JpaRepository<CampaignHotelParameter, Long> {

    @Query(value = "select distinct chp.*, cft.*, cht.* " +
            "from campaign_hotel_parameters chp " +
            "inner join campaign_hotel_parameters_ct_food_type_rel chpcftr on chp.id = chpcftr.campaign_hotel_parameter_id " +
            "inner join ct_food_types cft on cft.id = chpcftr.ct_food_type_id " +
            "inner join campaign_hotel_parameters_ct_hotel_type_rel chpchtr on chp.id = chpchtr.campaign_hotel_parameter_id " +
            "inner join ct_hotel_types cht on cht.id = chpchtr.ct_hotel_type_id " +
            "where chp.campaign_id = :campaignId", nativeQuery = true)
    List<CampaignHotelParameter> findByCampaignId(Long campaignId);

    @EntityGraph(attributePaths = {"city", "ctHotelTypes", "ctFoodTypes"} )
    Optional<CampaignHotelParameter> findById(long id);

    @Query(value = "delete from campaign_hotel_parameters where campaign_id =: campaignId", nativeQuery = true)
    void deleteByCampaignId(Long campaignId);

}
