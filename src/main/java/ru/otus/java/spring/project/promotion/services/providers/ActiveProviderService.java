package ru.otus.java.spring.project.promotion.services.providers;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.java.spring.project.promotion.configs.IntegrationPropertyFileConfig;
import ru.otus.java.spring.project.promotion.domains.providers.Provider;
import ru.otus.java.spring.project.promotion.exceptions.ApplicationException;
import ru.otus.java.spring.project.promotion.exceptions.ResourceNotFoundException;
import ru.otus.java.spring.project.promotion.repositories.providers.ProviderRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor

@Service("activeProviderService")
public class ActiveProviderService implements ProviderService {

    private final IntegrationPropertyFileConfig integrationPropertyFileConfig;

    private final ProviderRepository providerRepository;

    private static final Map<Long, Provider> ACTIVE_PROVIDER_CACHE = new HashMap<>();

    @PostConstruct
    private void initializeCache() {
        ACTIVE_PROVIDER_CACHE.putAll(readActiveProviderFromDbAndProperty());
        log.debug("Active providers initiated: {}", ACTIVE_PROVIDER_CACHE.values());
    }

    @Override
    public List<Provider> getAll() {
        return ACTIVE_PROVIDER_CACHE.values().stream().toList();
    }

    @Override
    public List<Provider> getByIds(List<Long> ids) {
        return ACTIVE_PROVIDER_CACHE.values().stream().filter(provider -> ids.contains(provider.getId())).toList();
    }

    @Override
    public Provider getById(long id) {
        return ACTIVE_PROVIDER_CACHE.get(id);
    }

    @Override
    public Provider getByPropertyName(String propertyName) {
        return ACTIVE_PROVIDER_CACHE.values().stream().filter(provider -> provider.getPropertyName().equals(propertyName))
                .findFirst()
                .orElseGet(() -> providerRepository.findByPropertyNameIgnoreCase(propertyName)
                        .orElseThrow(() -> new ResourceNotFoundException("Provider not found")));
    }

    private Map<Long, Provider> readActiveProviderFromDbAndProperty() {
        Map<Long, Provider> providerMap = providerRepository.findByIsActiveTrue().stream()
                .map(dbProvider -> integrationPropertyFileConfig.getProviders().entrySet().stream()
                        .filter(propertyProvider -> propertyProvider.getKey().equalsIgnoreCase(dbProvider.getPropertyName()))
                        .findFirst().map(entry -> {
                                    IntegrationPropertyFileConfig.IntegrationServiceProperties fileProperty = entry.getValue();
                                    if (fileProperty.getUrl() != null) {
                                        dbProvider.setApiUrl(fileProperty.getUrl());
                                    }
                                    if (fileProperty.getConnectTimeout() != null) {
                                        dbProvider.setConnectTimeout(fileProperty.getConnectTimeout());
                                    }
                                    if (fileProperty.getReadTimeout() != null) {
                                        dbProvider.setReadTimeout(fileProperty.getReadTimeout());
                                    }
                                    if (fileProperty.isEnable() != dbProvider.getIsActive()) {
                                        dbProvider.setIsActive(fileProperty.isEnable());
                                    }
                                    return dbProvider;
                                }
                        ).orElse(dbProvider))
                .filter(provider -> provider.getIsActive().equals(true))
                .collect(Collectors.toMap(Provider::getId, k -> k));
        if (providerMap.isEmpty()) {
            throw new ApplicationException("No enables providers in configuration");
        }
        return providerMap;
    }

    public List<Long> checkDisableProviders(List<Long> providersIds) {
        return providersIds.stream().filter(providerId -> !ACTIVE_PROVIDER_CACHE.containsKey(providerId)).toList();
    }

    public void refreshProviders() {
        initializeCache();
    }
}
