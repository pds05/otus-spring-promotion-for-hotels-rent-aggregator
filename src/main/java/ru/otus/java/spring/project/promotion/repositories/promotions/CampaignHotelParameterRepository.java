package ru.otus.java.spring.project.promotion.repositories.promotions;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.otus.java.spring.project.promotion.domains.promotions.CampaignHotelParameter;

import java.util.List;
import java.util.Optional;

public interface CampaignHotelParameterRepository extends JpaRepository<CampaignHotelParameter, Long> {

    @Query("SELECT DISTINCT chp FROM CampaignHotelParameter chp " +
            "JOIN FETCH chp.ctFoodTypes " +
            "JOIN FETCH chp.ctHotelTypes " +
            "JOIN FETCH chp.city " +
            "WHERE chp.campaignId = :campaignId")
    List<CampaignHotelParameter> findByCampaignId(Long campaignId);

    @EntityGraph(attributePaths = {"city", "ctHotelTypes", "ctFoodTypes"} )
    Optional<CampaignHotelParameter> findById(long id);

    void deleteByCampaignId(Long campaignId);

}
