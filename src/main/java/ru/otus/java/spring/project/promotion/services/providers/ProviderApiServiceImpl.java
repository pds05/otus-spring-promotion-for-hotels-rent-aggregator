package ru.otus.java.spring.project.promotion.services.providers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.java.spring.project.promotion.enums.BusinessMethodEnum;
import ru.otus.java.spring.project.promotion.domains.providers.ProviderApi;
import ru.otus.java.spring.project.promotion.exceptions.ResourceNotFoundException;
import ru.otus.java.spring.project.promotion.repositories.providers.ProviderApiRepository;

import java.util.List;

@RequiredArgsConstructor
@Service("providerApiService")
public class ProviderApiServiceImpl implements ProviderApiService {

    private final ProviderApiRepository providerApiRepository;

    private final ProviderService providerService;

    @Override
    public ProviderApi getById(long id) {
        return providerService.getAll().stream().flatMap(provider -> provider.getProviderApis().stream()).filter(api -> api.getId().equals(id))
                .findFirst()
                .orElseGet(() -> providerApiRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("ProviderApi with id " + id + " not found")));
    }

    @Override
    public List<ProviderApi> getByProviderId(long providerId) {
        var cached = providerService.getById(providerId);

        if (cached != null) {
            return cached.getProviderApis();
        } else {
            return providerApiRepository.findAllByProviderId(providerId);
        }
    }

    @Override
    public ProviderApi getByProviderIdAndBusinessMethod(long providerId, BusinessMethodEnum businessMethod) {
        var cachedProvider = providerService.getById(providerId);

        if (cachedProvider != null) {
            var cachedApi = cachedProvider.getProviderApi(businessMethod);
            if (cachedApi != null) {
                return cachedApi;
            }
        }
        return providerApiRepository.findByProviderIdAndBusinessMethod(providerId, businessMethod)
                .orElseThrow(() -> new ResourceNotFoundException("ProviderApi with providerId " + providerId + " and businessMethod " + businessMethod.name() + " not found"));
    }

    @Override
    public List<ProviderApi> getAll() {
        var cached = providerService.getAll().stream().flatMap(provider -> provider.getProviderApis().stream()).toList();
        return !cached.isEmpty() ? cached : providerApiRepository.findAll();
    }
}
