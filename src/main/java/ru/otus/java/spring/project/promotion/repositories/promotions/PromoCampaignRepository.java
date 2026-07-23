package ru.otus.java.spring.project.promotion.repositories.promotions;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.java.spring.project.promotion.domains.promotions.PromoCampaign;
import ru.otus.java.spring.project.promotion.domains.promotions.PromoCampaignStatus;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PromoCampaignRepository extends JpaRepository<PromoCampaign, Long> {

    void deleteById(Long id);

    @EntityGraph(attributePaths = {"hotelParameters", "hotelParameters.ctHotelTypes", "hotelParameters.ctFoodTypes" ,"campaignProviders"})
    Optional<PromoCampaign> findFirstByStartDateBeforeAndStatus(LocalDateTime startDate, PromoCampaignStatus status);
}
