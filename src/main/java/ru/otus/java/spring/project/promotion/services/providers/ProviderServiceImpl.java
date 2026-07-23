package ru.otus.java.spring.project.promotion.services.providers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.java.spring.project.promotion.domains.providers.Provider;
import ru.otus.java.spring.project.promotion.exceptions.ResourceNotFoundException;
import ru.otus.java.spring.project.promotion.repositories.providers.ProviderRepository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor

@Service("providerService")
public class ProviderServiceImpl implements ProviderService {

    private final ProviderRepository providerRepository;

    private static final Map<Long, Provider> PROVIDER_CACHE = new HashMap<>();

    @Override
    public List<Provider> getAll() {
        if (PROVIDER_CACHE.isEmpty()) {
            List<Provider> updated = providerRepository.findAll();
            if (updated.isEmpty()) {
                throw new ResourceNotFoundException("Providers not found");
            }
            PROVIDER_CACHE.putAll(updated.stream().collect(Collectors.toMap(Provider::getId, p -> p)));
        }
        return PROVIDER_CACHE.values().stream().toList();
    }

    @Override
    public List<Provider> getByIds(List<Long> ids) {
        List<Provider> result = PROVIDER_CACHE.entrySet().stream().filter(entry -> ids.contains(entry.getKey())).map(Map.Entry::getValue).toList();
        if (result.size() != ids.size()) {
            List<Long> query = ids.stream().filter(id -> !PROVIDER_CACHE.containsKey(id)).toList();
            List<Provider> updated = providerRepository.findByIdIn(query);

            PROVIDER_CACHE.putAll(updated.stream().collect(Collectors.toMap(Provider::getId, p -> p)));
            return updated;
        }
        if (result.isEmpty()) {
            throw new ResourceNotFoundException("Providers not found for ids " + ids);
        }
        return result;
    }

    @Override
    public Provider getById(long id) {
        Provider provider = PROVIDER_CACHE.get(id);
        if (provider == null) {
            provider = providerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Provider " + id + " not found"));
            PROVIDER_CACHE.put(id, provider);
            return provider;
        } else {
            return provider;
        }
    }

    @Override
    public Provider getByPropertyName(String propertyName) {
        Provider result = PROVIDER_CACHE.values().stream().filter(provider -> provider.getPropertyName().equals(propertyName))
                .findFirst()
                .orElseGet(() -> providerRepository.findByPropertyNameIgnoreCase(propertyName)
                        .orElseThrow(() -> new ResourceNotFoundException("Provider not found")));
        PROVIDER_CACHE.putIfAbsent(result.getId(), result);
        return result;
    }
}
