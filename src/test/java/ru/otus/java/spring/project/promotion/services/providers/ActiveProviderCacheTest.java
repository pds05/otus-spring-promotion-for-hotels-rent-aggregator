package ru.otus.java.spring.project.promotion.services.providers;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Component;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.java.spring.project.promotion.controllers.mvc.PromoCampaignMvcController;
import ru.otus.java.spring.project.promotion.controllers.rest.ProviderRestController;
import ru.otus.java.spring.project.promotion.exceptions.ResourceNotFoundException;
import ru.otus.java.spring.project.promotion.services.cache.ActiveProviderCache;
import ru.otus.java.spring.project.promotion.configs.IntegrationPropertyFileConfig;
import ru.otus.java.spring.project.promotion.domains.providers.Provider;
import ru.otus.java.spring.project.promotion.integrations.ProviderRestClient;
import ru.otus.java.spring.project.promotion.integrations.TelegramRestClient;
import ru.otus.java.spring.project.promotion.repositories.providers.ProviderRepository;
import ru.otus.java.spring.project.promotion.tasks.PromoCampaignExecutor;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@DisplayName("Сервис для работы с настройками провайдеров")
@SpringBootTest
@EnableAutoConfiguration(exclude = WebMvcAutoConfiguration.class)

public class ActiveProviderCacheTest {

    @Autowired
    private ProviderServiceImpl providerService;

    @Autowired
    private ActiveProviderCache activeProviderCache;

    @MockitoBean
    private ProviderRepository providerRepository;

    @MockitoBean
    private IntegrationPropertyFileConfig integrationPropertyFileConfig;

    @MockitoBean
    private PromoCampaignExecutor promoCampaignExecutor;

    @MockitoBean
    private ProviderRestClient providerRestClient;

    @MockitoBean
    private TelegramRestClient telegramRestClient;

    @MockitoBean
    private ProviderRestController providerRestController;

    @MockitoBean
    private PromoCampaignMvcController promoCampaignMvcController;

    private Provider repoProviderNifNif;

    private Provider repoProviderNafNaf;

    private Provider repoProviderNufNuf;

    private Map<String, IntegrationPropertyFileConfig.IntegrationServiceProperties> propertyProviders;

    @BeforeEach
    void initProviders() {
        propertyProviders = new HashMap<>();

        repoProviderNifNif = new Provider();
        repoProviderNifNif.setId(1L);
        repoProviderNifNif.setPropertyName("nifnif");
        repoProviderNifNif.setTitle("Nif Nif");
        repoProviderNifNif.setDescription("Nif Nif Provider");
        repoProviderNifNif.setIsActive(true);

        repoProviderNafNaf = new Provider();
        repoProviderNafNaf.setId(2L);
        repoProviderNafNaf.setPropertyName("nafnaf");
        repoProviderNafNaf.setTitle("Naf Naf");
        repoProviderNafNaf.setDescription("Naf Naf Provider");
        repoProviderNafNaf.setIsActive(true);

        repoProviderNufNuf = new Provider();
        repoProviderNufNuf.setId(3L);
        repoProviderNufNuf.setPropertyName("nufnuf");
        repoProviderNufNuf.setTitle("Nuf Nuf");
        repoProviderNufNuf.setDescription("Nuf Nuf Provider");
        repoProviderNufNuf.setIsActive(true);

        IntegrationPropertyFileConfig.IntegrationServiceProperties disabledProviderProperty = new IntegrationPropertyFileConfig.IntegrationServiceProperties();
        disabledProviderProperty.setEnable(false);
        propertyProviders.put("nufnuf", disabledProviderProperty);
    }

    @DisplayName("Должен создавать кэш активных провайдеров")
    @Test
    void shouldInitCache(){
        when(providerRepository.findAll()).thenReturn(List.of(repoProviderNifNif, repoProviderNafNaf, repoProviderNufNuf));
        when(integrationPropertyFileConfig.getProviders()).thenReturn(propertyProviders);

        assertThat(activeProviderCache).isNotNull().isEqualTo(List.of(repoProviderNifNif, repoProviderNafNaf));
    }

    @DisplayName("Должен вернуть кэшированный провайдер")
    @Test
    void shouldReturnProviderFromCache() {

        var expected = activeProviderCache.get(1L);
        assertThat(expected).isEqualTo(repoProviderNifNif);
    }

    @DisplayName("Должен вернуть провайдерa из внешней базы")
    @Test
    void shouldReturnProviderFromDb() {
        when(providerService.getById(1L)).thenReturn(null);
        when(providerRepository.findById(1L)).thenReturn(Optional.of(repoProviderNifNif));

        var expected = providerService.getById(1L);
        assertThat(expected).isEqualTo(repoProviderNifNif);
    }

    @DisplayName("Должен вернуть ошибку по неизвестному провайдеру")
    @Test
    void shouldReturnError() {
        when(providerService.getById(1L)).thenReturn(null);
        when(providerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> providerService.getById(1L));
    }

    @DisplayName("Должен вернуть все кэшированные провайдеры")
    @Test
    void shouldReturnAllProvidersFromCache() {
        when(providerService.getAll()).thenReturn(List.of(repoProviderNifNif, repoProviderNafNaf, repoProviderNufNuf));

        var providers = providerService.getAll();
        assertThat(providers.size()).isEqualTo(3);
        assertThat(providers).contains(repoProviderNifNif, repoProviderNafNaf, repoProviderNufNuf);
    }

    @DisplayName("Должен вернуть все провайдеры из базы")
    @Test
    void shouldReturnAllProvidersFromDb() {
        when(providerService.getAll()).thenReturn(Collections.emptyList());
        when(providerRepository.findAll()).thenReturn(List.of(repoProviderNifNif, repoProviderNafNaf, repoProviderNufNuf));

        var providers = providerService.getAll();
        assertThat(providers.size()).isEqualTo(3);
        assertThat(providers).contains(repoProviderNifNif, repoProviderNafNaf, repoProviderNufNuf);
    }

    @Test
    @DisplayName("Должен вернуть список провайдеров и обновить кэш")
    void shouldReturnProvidersAndSyncCacheAndDb() {
        when(providerService.getAll()).thenReturn(List.of(repoProviderNifNif, repoProviderNafNaf));
        when(providerRepository.findByIdIn(any())).thenReturn(List.of(repoProviderNufNuf));

        var providers = providerService.getByIds(List.of(1L, 2L, 3L));
        assertThat(providers.size()).isEqualTo(3);
        assertThat(providers).contains(repoProviderNifNif, repoProviderNafNaf, repoProviderNufNuf);
    }

    @DisplayName("Должен вернуть провайдера из кэша по имени")
    @Test
    void shouldReturnProviderByPropertyNameFromCache() {
        when(providerService.getAll()).thenReturn(List.of(repoProviderNifNif, repoProviderNafNaf, repoProviderNufNuf));

        var provider = providerService.getByPropertyName("nifnif");
        assertThat(provider).isEqualTo(repoProviderNifNif);
    }

    @DisplayName("Должен вернуть провайдера из базы по имени")
    @Test
    void shouldReturnProviderByPropertyNameFromDb() {
        when(providerService.getAll()).thenReturn(List.of(repoProviderNifNif, repoProviderNafNaf));
        when(providerRepository.findByPropertyNameIgnoreCase(anyString())).thenReturn(Optional.of(repoProviderNufNuf));

        var provider = providerService.getByPropertyName("nufnuf");
        assertThat(provider).isEqualTo(repoProviderNufNuf);
    }
}
