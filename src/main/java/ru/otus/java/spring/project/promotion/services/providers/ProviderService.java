package ru.otus.java.spring.project.promotion.services.providers;

import ru.otus.java.spring.project.promotion.domains.providers.Provider;

import java.util.List;

public interface ProviderService {

    List<Provider> getAll();

    List<Provider> getByIds(List<Long> ids);

    Provider getById(long id);

    Provider getByPropertyName(String propertyName);

}
