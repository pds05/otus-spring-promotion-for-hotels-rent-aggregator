package ru.otus.java.spring.project.promotion.services.promotions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.java.spring.project.promotion.domains.providers.Provider;
import ru.otus.java.spring.project.promotion.dtos.response.ProviderHotelDataDto;
import ru.otus.java.spring.project.promotion.integrations.ProviderRestClient;
import ru.otus.java.spring.project.promotion.integrations.TelegramRestClient;
import ru.otus.java.spring.project.promotion.services.cache.ActiveProviderCache;
import ru.otus.java.spring.project.promotion.services.providers.ProviderServiceImpl;
import ru.otus.java.spring.project.promotion.tasks.PromoCampaignExecutor;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DisplayName("Сервис для работы с топ-предложениями")
@SpringBootTest
@EnableAutoConfiguration(exclude = WebMvcAutoConfiguration.class)
public class ProviderHotelDataServiceImplTest {

    public static final long PROMO_CAMPAIGN_LOW_COST = 101L;

    @Autowired
    private ProviderHotelDataServiceImpl providerHotelDataService;

    @MockitoBean
    private ProviderServiceImpl providerService;

    @MockitoBean
    private ActiveProviderCache activeProviderCache;

    @MockitoBean
    private ProviderRestClient providerRestClient;

    @MockitoBean
    private TelegramRestClient telegramRestClient;

    @MockitoBean
    private PromoCampaignExecutor promoCampaignExecutor;

    private final List<ProviderHotelDataDto> top = List.of(
            new ProviderHotelDataDto(1L, "Сервис", "Москва", "Гостиница", "Номер", "Тариф", "1000", 2, "Питание", LocalDate.now(), LocalDate.now(), LocalDateTime.now()),
            new ProviderHotelDataDto(1L, "Сервис 2", "Москва", "Гостиница 2", "Номер 2", "Тариф 2", "1000", 2, "Питание", LocalDate.now(), LocalDate.now(), LocalDateTime.now()));

    @DisplayName("Должен вернуть топ предложений для кампании")
    @Test
    public void shouldReturnTopData(){
        when(providerService.getById(1L)).thenReturn(new Provider(1L, "Провайдер 1", "Сервис 1", null, null, null, null, true, Duration.ZERO, Duration.ZERO, LocalDateTime.now(), LocalDateTime.now(), null));
        when(providerService.getById(2L)).thenReturn(new Provider(1L, "Провайдер 2", "Сервис 2", null, null, null, null, true, Duration.ZERO, Duration.ZERO, LocalDateTime.now(), LocalDateTime.now(), null));

        var topList = providerHotelDataService.getTop(PROMO_CAMPAIGN_LOW_COST);

        assertThat(topList).size().isEqualTo(2);
    }

    @DisplayName("Должен удалить топ предложений для кампании")
    @Test
    public void shouldDeleteTopData(){
        providerHotelDataService.deleteByCampaignId(PROMO_CAMPAIGN_LOW_COST);
        var empty = providerHotelDataService.getTop(PROMO_CAMPAIGN_LOW_COST);

        assertThat(empty).isEmpty();
    }
}
