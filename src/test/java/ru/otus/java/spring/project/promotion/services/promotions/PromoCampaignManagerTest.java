package ru.otus.java.spring.project.promotion.services.promotions;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import ru.otus.java.spring.project.promotion.domains.promotions.PromoCampaignResult;
import ru.otus.java.spring.project.promotion.domains.promotions.PromoCampaignStatus;
import ru.otus.java.spring.project.promotion.domains.promotions.PromoCampaignType;
import ru.otus.java.spring.project.promotion.domains.providers.Provider;
import ru.otus.java.spring.project.promotion.dtos.request.CampaignHotelParameterRqDto;
import ru.otus.java.spring.project.promotion.dtos.request.PromoCampaignRqDto;
import ru.otus.java.spring.project.promotion.exceptions.ResourceNotFoundException;
import ru.otus.java.spring.project.promotion.integrations.ProviderRestClient;
import ru.otus.java.spring.project.promotion.integrations.TelegramRestClient;
import ru.otus.java.spring.project.promotion.services.providers.ActiveProviderService;
import ru.otus.java.spring.project.promotion.services.providers.ProviderServiceImpl;
import ru.otus.java.spring.project.promotion.tasks.PromoCampaignExecutor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

@DisplayName("Сервис для работы с промо-кампаниями")
@SpringBootTest
public class PromoCampaignManagerTest {

    public static final long MIGRATED_PROMO_CAMPAIGN_ID = 100L;

    @Autowired
    private PromoCampaignManager promoCampaignService;

    @MockitoBean
    private ProviderServiceImpl providerService;

    @MockitoBean
    private ActiveProviderService activeProviderService;

    @MockitoBean
    private ProviderRestClient providerRestClient;

    @MockitoBean
    private TelegramRestClient telegramRestClient;

    @MockitoBean
    private PromoCampaignExecutor promoCampaignExecutor;

    private Provider nifNif;

    private Provider nafNaf;

    @BeforeEach
     void initProviders(){
        nifNif = new Provider();
        nifNif.setId(1L);
        nifNif.setPropertyName("nifnif");
        nifNif.setTitle("Nif Nif");
        nifNif.setDescription("Nif Nif Provider");

        nafNaf = new Provider();
        nafNaf.setId(2L);
        nafNaf.setPropertyName("nafnaf");
        nafNaf.setTitle("Naf Naf");
        nafNaf.setDescription("Naf Naf Provider");

        when(providerService.getById(1L)).thenReturn(nifNif);
        when(providerService.getById(2L)).thenReturn(nafNaf);
        when(providerService.getByIds(anyList())).thenReturn(List.of(nifNif, nafNaf));
    }

    @DisplayName("Должен создавать новую промо-кампанию")
    @Transactional
    @Test
    void shouldSavePromoCampaign() {
        PromoCampaignRqDto request = createPromoCampaignRequest();
        var saved = promoCampaignService.save(request);

        assertThat(saved).isNotNull().matches( campaign -> campaign.getId() > 0 )
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(promoCampaignService.get(saved.getId()));
        assertThat(saved.getHotelParameters().size()).isEqualTo(2);
        assertThat(saved.getHotelParameters().get(0).getId()).isNotNull();
        assertThat(saved.getHotelParameters().get(1).getId()).isNotNull();
        assertThat(saved.getHotelParameters().stream().filter(p -> p.getCityName().equals("Москва")).findFirst().get().getHotelTypes().size()).isEqualTo(2);
        assertThat(saved.getHotelParameters().stream().filter(p -> p.getCityName().equals("Казань")).findFirst().get().getHotelTypes().size()).isEqualTo(2);
        assertThat(saved.getHotelParameters().stream().filter(p -> p.getCityName().equals("Москва")).findFirst().get().getFoodTypes().size()).isEqualTo(4);
        assertThat(saved.getHotelParameters().stream().filter(p -> p.getCityName().equals("Казань")).findFirst().get().getFoodTypes().size()).isEqualTo(1);
        assertThat(saved.getProviders().size()).isEqualTo(2);
    }

    @Transactional
    @Test
    @DisplayName("Должен запускать промо-кампанию")
    void shouldStartPromoCampaign() {
        var created = promoCampaignService.get(MIGRATED_PROMO_CAMPAIGN_ID);

        var started = promoCampaignService.start(created.getId());

        assertThat(started).isNotNull().matches( campaign -> campaign.getId() > 0 )
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(promoCampaignService.get(started.getId()));
        assertThat(started.getStatus()).isEqualTo(PromoCampaignStatus.READY.getDescription());
    }

    @Test
    @DisplayName("Должен возвращать промо-кампании по Id")
    void shouldReturnPromoCampaignById() {
        var expected = promoCampaignService.get(MIGRATED_PROMO_CAMPAIGN_ID);

        assertThat(expected.getId()).isNotNull();
        assertThat(expected.getTitle()).isEqualTo("Кампания по низкой стоимости");
        assertThat(expected.getHotelParameters().size()).isEqualTo(2);
        assertThat(expected.getHotelParameters().get(0).getId()).isNotNull();
        assertThat(expected.getHotelParameters().get(1).getId()).isNotNull();
        assertThat(expected.getProviders().size()).isEqualTo(2);
        assertThat(expected.getHotelParameters().get(0).getHotelTypes().size()).isEqualTo(2);
        assertThat(expected.getHotelParameters().stream().filter(p -> p.getCityName().equals("Москва")).findFirst().get().getHotelTypes().size()).isEqualTo(2);
    }

    @Transactional
    @Test
    @DisplayName("Должен останавливать промо-кампанию в ожидании выполнения")
    void shouldStopPromoCampaign() {
        var created = promoCampaignService.get(MIGRATED_PROMO_CAMPAIGN_ID);

        promoCampaignService.start(created.getId());
        var stopped = promoCampaignService.stop(created.getId());

        assertThat(stopped).isNotNull().matches( campaign -> campaign.getId() > 0 )
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(promoCampaignService.get(stopped.getId()));
        assertThat(stopped.getStatus()).isEqualTo(PromoCampaignStatus.IDLE.getDescription());
        assertThat(stopped.getResult()).isEqualTo(PromoCampaignResult.INTERRUPTED.getDescription());
    }

    @Transactional
    @Test
    @DisplayName("Должен менять статус и успешно останавливать кампанию")
    void shouldChangeStatusAndStopNormally() {
        var created = promoCampaignService.get(MIGRATED_PROMO_CAMPAIGN_ID);
        var working = promoCampaignService.changeStatus(created.getId(), PromoCampaignStatus.IN_PROGRESS, null);

        assertThat(working).isNotNull().matches( campaign -> campaign.getId() > 0 )
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(promoCampaignService.get(working.getId()));
        assertThat(working.getStatus()).isEqualTo(PromoCampaignStatus.IN_PROGRESS.getDescription());

        var stopped = promoCampaignService.stop(working.getId());

        assertThat(stopped).isNotNull().matches( campaign -> campaign.getId() > 0 )
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(promoCampaignService.get(stopped.getId()));
        assertThat(stopped.getStatus()).isEqualTo(PromoCampaignStatus.COMPLETED.getDescription());
        assertThat(stopped.getResult()).isEqualTo(PromoCampaignResult.OK.getDescription());
    }

    @Transactional
    @Test
    @DisplayName("Должен прерывать промо-кампанию по ошибке")
    void shouldAbortPromoCampaign() {
        var created = promoCampaignService.get(MIGRATED_PROMO_CAMPAIGN_ID);

        promoCampaignService.start(created.getId());
        var aborted = promoCampaignService.abort(created.getId(), "Провайдер не доступен");

        assertThat(aborted).isNotNull().matches( campaign -> campaign.getId() > 0 )
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(promoCampaignService.get(aborted.getId()));
        assertThat(aborted.getStatus()).isEqualTo(PromoCampaignStatus.COMPLETED.getDescription());
        assertThat(aborted.getResult()).isEqualTo(PromoCampaignResult.NOK_FAILED.getDescription());
    }

    @DisplayName("Должен удалять промо кампанию")
    @Transactional
    @Test
    void shouldDeletePromoCampaign() {
        var expected = promoCampaignService.get(MIGRATED_PROMO_CAMPAIGN_ID);
        promoCampaignService.delete(expected.getId());

        assertThrows(ResourceNotFoundException.class, () -> promoCampaignService.get(expected.getId()));
    }

    private PromoCampaignRqDto createPromoCampaignRequest() {
        PromoCampaignRqDto request = new PromoCampaignRqDto();
        request.setStartDate(LocalDateTime.of(2026, Month.AUGUST, 1, 12, 0));
        request.setTitle("Тестовая кампания");
        request.setCampaignType(PromoCampaignType.LOW_COST.getDescription());
        request.setProviderIds(List.of(1L, 2L));

        CampaignHotelParameterRqDto moscowParameter = new CampaignHotelParameterRqDto();
        moscowParameter.setCityName("Москва");
        moscowParameter.setGuests(2);
        moscowParameter.setDateIn(LocalDate.of(2026, Month.AUGUST, 2));
        moscowParameter.setDateOut(LocalDate.of(2026, Month.AUGUST, 3));
        moscowParameter.setHotelTypeIds(List.of(1L, 3L));
        moscowParameter.setFoodTypeIds(List.of(1L, 2L, 3L, 4L));

        CampaignHotelParameterRqDto kazanParameter = new CampaignHotelParameterRqDto();
        kazanParameter.setCityName("Казань");
        kazanParameter.setGuests(4);
        kazanParameter.setDateIn(LocalDate.of(2026, Month.AUGUST, 2));
        kazanParameter.setDateOut(LocalDate.of(2026, Month.AUGUST, 3));
        kazanParameter.setHotelTypeIds(List.of(1L, 3L));
        kazanParameter.setFoodTypeIds(List.of(5L));

        request.setHotelParameters(List.of(moscowParameter, kazanParameter));
        return request;
    }
}
