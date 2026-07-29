package ru.otus.java.spring.project.promotion.services.cache;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.java.spring.project.promotion.configs.IntegrationPropertyFileConfig;
import ru.otus.java.spring.project.promotion.domains.providers.Provider;
import ru.otus.java.spring.project.promotion.exceptions.ApplicationException;
import ru.otus.java.spring.project.promotion.exceptions.ResourceNotFoundException;
import ru.otus.java.spring.project.promotion.repositories.providers.ProviderRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service("activeProviderCache")
public class ActiveProviderCache implements ModelCache<Provider> {

    private final IntegrationPropertyFileConfig integrationPropertyFileConfig;

    private final ProviderRepository providerRepository;

    private static final Map<Long, Provider> CACHE = new HashMap<>();

    @PostConstruct
    private void init() {
        CACHE.putAll(readActiveProviderFromDbAndProperty());
        log.debug("Active providers initiated: {}", CACHE.values());
    }

    @Override
    public void put(Provider provider) {
        CACHE.put(provider.getId(), provider);
    }

    @Override
    public void remove(long id) {
        CACHE.remove(id);
    }

    @Override
    public int size() {
        return CACHE.size();
    }

    @Override
    public boolean isEmpty() {
        return CACHE.isEmpty();
    }

    @Override
    public void putAll(Collection<Provider> models) {
        CACHE.putAll(models.stream().collect(Collectors.toMap(Provider::getId, c -> c)));
    }

    @Override
    public List<Provider> getAll() {
        return CACHE.values().stream().toList();
    }

    @Override
    public List<Provider> getByIds(Collection<Long> ids) {
        return CACHE.values().stream().filter(provider -> ids.contains(provider.getId())).toList();
    }

    @Override
    public Provider get(long id) {
        return CACHE.get(id);
    }

    public Provider getByPropertyName(String propertyName) {
        return CACHE.values().stream().filter(provider -> provider.getPropertyName().equals(propertyName))
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
            log.error("No active providers in configuration property or database");
        }
        return providerMap;
    }

    public List<Long> checkDisableProviders(List<Long> providersIds) {
        return providersIds.stream().filter(providerId -> !CACHE.containsKey(providerId)).toList();
    }

    public void refreshProviders() {
        init();
    }
}
