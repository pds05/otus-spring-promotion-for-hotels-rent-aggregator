package ru.otus.java.spring.project.promotion.services.providers;

import ru.otus.java.spring.project.promotion.enums.BusinessMethodEnum;
import ru.otus.java.spring.project.promotion.domains.providers.ProviderApi;

import java.util.List;

public interface ProviderApiService {

    ProviderApi getById(long id);

    ProviderApi getByProviderIdAndBusinessMethod(long providerId, BusinessMethodEnum businessMethod);

    List<ProviderApi> getByProviderId(long providerId);

    List<ProviderApi> getAll();
}
