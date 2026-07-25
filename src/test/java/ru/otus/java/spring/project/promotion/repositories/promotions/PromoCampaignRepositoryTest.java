package ru.otus.java.spring.project.promotion.repositories.promotions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.java.spring.project.promotion.domains.promotions.*;
import ru.otus.java.spring.project.promotion.enums.PromoCampaignStatus;
import ru.otus.java.spring.project.promotion.enums.PromoCampaignType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Репозиторий JPA для работы с промо-кампаниями")
@DataJpaTest
public class PromoCampaignRepositoryTest {

    public static final long PROMO_CAMPAIGN_LOW_COST = 101L;

    @Autowired
    private PromoCampaignRepository promoCampaignRepository;

    @Autowired
    private TestEntityManager entityManager;

    @DisplayName("Должен загружать кампанию по id")
    @Test
    void shouldReturnPromoCampaignById() {
        var optionalPromoCampaign = promoCampaignRepository.findById(PROMO_CAMPAIGN_LOW_COST);

        var expectedPromoCampaign = entityManager.find(PromoCampaign.class, PROMO_CAMPAIGN_LOW_COST);

        assertThat(optionalPromoCampaign).isPresent()
                .get()
                .isEqualTo(expectedPromoCampaign);
        assertThat(optionalPromoCampaign.get().getTitle()).isEqualTo("Кампания по низкой стоимости");
    }

    @Transactional
    @DisplayName("Должен сохранять новую кампанию")
    @Test
    void shouldSavePromoCampaign() {
        PromoCampaign expectedCampaign = new PromoCampaign();
        expectedCampaign.setTitle("Новая промо кампания");
        expectedCampaign.setCampaignType(PromoCampaignType.LOW_COST);
        expectedCampaign.setStartDate(LocalDateTime.of(2027, 1, 1, 12,0));
        expectedCampaign.setCampaignProviders(Set.of(
                entityManager.find(CampaignProvider.class, 1L),
                entityManager.find(CampaignProvider.class, 2L)));

        CampaignHotelParameter moscow = new CampaignHotelParameter();
        moscow.setDateIn(LocalDate.of(2027, 1, 2));
        moscow.setDateOut(LocalDate.of(2027, 1, 3));
        moscow.setCity(entityManager.find(CtCity.class, 1L));
        moscow.setGuests(2);
        moscow.setCtHotelTypes(Set.of(
                entityManager.find(CtHotelType.class, 1L),
                entityManager.find(CtHotelType.class, 3L)));
        moscow.setCtFoodTypes(Set.of(
                entityManager.find(CtFoodType.class, 1L),
                entityManager.find(CtFoodType.class, 2L),
                entityManager.find(CtFoodType.class, 3L),
                entityManager.find(CtFoodType.class, 4L)
        ));

        CampaignHotelParameter kazan = new CampaignHotelParameter();
        kazan.setDateIn(LocalDate.of(2027, 1, 2));
        kazan.setDateOut(LocalDate.of(2027, 1, 3));
        kazan.setCity(entityManager.find(CtCity.class, 3L));
        kazan.setGuests(2);
        kazan.setCtHotelTypes(Set.of(entityManager.find(CtHotelType.class, 1L),
                entityManager.find(CtHotelType.class, 3L)));
        moscow.setCtFoodTypes(Set.of(
                entityManager.find(CtFoodType.class, 5L)
        ));

        expectedCampaign.setHotelParameters(Set.of(moscow, kazan));

        var returnedPromoCampaign = promoCampaignRepository.save(expectedCampaign);

        assertThat(returnedPromoCampaign).isNotNull().matches(campaign -> campaign.getId() > 0L)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedCampaign);

        assertThat(entityManager.find(PromoCampaign.class, returnedPromoCampaign.getId()))
                .isEqualTo(expectedCampaign);
    }

    @Transactional
    @DisplayName("Должен обновлять параметры кампании")
    @Test
    void shouldUpdatePromoCampaign() {
        var optionalPromoCampaign = entityManager.find(PromoCampaign.class, PROMO_CAMPAIGN_LOW_COST);
        optionalPromoCampaign.setStartDate(LocalDateTime.of(2026, 12, 1, 12,0));
        optionalPromoCampaign.removeCampaignProvider(entityManager.find(CampaignProvider.class, 2L));
        optionalPromoCampaign.removeHotelParameter(optionalPromoCampaign.getHotelParameters().stream().toList().get(0));
        optionalPromoCampaign.setCampaignType(PromoCampaignType.LOW_COST_WITH_FOOD);
        optionalPromoCampaign.getHotelParameters().stream().toList().get(0).setId(2L);
        optionalPromoCampaign.setStatus(PromoCampaignStatus.READY);

        var updatedPromoCampaign = promoCampaignRepository.save(optionalPromoCampaign);

        assertThat(updatedPromoCampaign).isNotNull().matches(campaign -> campaign.getId() > 0L)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(optionalPromoCampaign);

        assertThat(updatedPromoCampaign).isEqualTo(optionalPromoCampaign);

        assertThat(entityManager.find(PromoCampaign.class, updatedPromoCampaign.getId()))
                .isEqualTo(optionalPromoCampaign);
    }

    @Transactional
    @DisplayName("Должен удалять кампанию по id")
    @Test
    void shouldDeletePromoCampaign() {
        var optionalPromoCampaign = entityManager.find(PromoCampaign.class, PROMO_CAMPAIGN_LOW_COST);

        promoCampaignRepository.deleteById(optionalPromoCampaign.getId());

        assertThat(entityManager.find(PromoCampaign.class, optionalPromoCampaign.getId())).isNull();
    }
}
