package ru.otus.java.spring.project.promotion.repositories.promotions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.java.spring.project.promotion.domains.promotions.PromoCampaign;
import ru.otus.java.spring.project.promotion.domains.promotions.ProviderHotelData;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий JPA для работы с данными запроса провайдера")
@DataJpaTest
public class ProviderHotelDataRepositoryTest {

    public static final long MIGRATED_PROMO_CAMPAIGN_ID = 100L;

    @Autowired
    private ProviderHotelDataRepository providerHotelDataRepository;

    @Autowired
    private TestEntityManager entityManager;

    @DisplayName("Должен читать данные предложений по id кампании")
    @Test
    void shouldGetDataByCampaignId(){
        var response = providerHotelDataRepository.findByPromoCampaignId(MIGRATED_PROMO_CAMPAIGN_ID);

        assertThat(response).isNotEmpty();
    }

    @DisplayName("Должен находить предложения с минимальной стоимостью")
    @Test
    void shouldGetDataByCampaignWithMinPrice(){
        var hotelWithFood = providerHotelDataRepository.findWithMinPrice(MIGRATED_PROMO_CAMPAIGN_ID, "Москва");

        assertThat(hotelWithFood).isNotEmpty();
        assertThat(hotelWithFood).allMatch(data -> data.getCityName().equals("Москва"));
    }

    @DisplayName("Должен находить предложения с минимальной стоимостью и питанием")
    @Test
    void shouldGetDataByCampaignWithMinPriceAndFood(){
        var allInclusive = providerHotelDataRepository.findWithMinPriceAndFood(MIGRATED_PROMO_CAMPAIGN_ID, "Москва", List.of("Все включено"));

        assertThat(allInclusive).isNotEmpty();
        assertThat(allInclusive).allMatch(data -> data.getFood().equals("Все включено") && data.getCityName().equals("Москва"));
    }

    @Transactional
    @DisplayName("Должен сохранять данные предложений")
    @Test
    void shouldSaveData(){
        ProviderHotelData expectedData = new ProviderHotelData();
        expectedData.setPromoCampaign(entityManager.find(PromoCampaign.class, MIGRATED_PROMO_CAMPAIGN_ID));
        expectedData.setCityName("Москва");
        expectedData.setProviderId(1L);
        expectedData.setHotelId(10L);
        expectedData.setHotelRoomId(100L);

        var savedData = providerHotelDataRepository.save(expectedData);

        assertThat(savedData).isNotNull().matches(campaign -> campaign.getId() > 0L)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedData);

        assertThat(entityManager.find(ProviderHotelData.class, savedData.getId()))
                .isEqualTo(expectedData);

    }

    @Transactional
    @DisplayName("Должен удалять данные предложений по id кампании")
    @Test
    void shouldDeleteData(){
        assertThat(providerHotelDataRepository.findByPromoCampaignId(MIGRATED_PROMO_CAMPAIGN_ID)).isNotEmpty();

        providerHotelDataRepository.deleteByPromoCampaignId(MIGRATED_PROMO_CAMPAIGN_ID);

        assertThat(providerHotelDataRepository.findByPromoCampaignId(MIGRATED_PROMO_CAMPAIGN_ID)).isEmpty();
    }

}
