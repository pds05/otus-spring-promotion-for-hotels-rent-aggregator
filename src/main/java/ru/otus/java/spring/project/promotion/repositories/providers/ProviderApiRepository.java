package ru.otus.java.spring.project.promotion.repositories.providers;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.java.spring.project.promotion.domains.promotions.BusinessMethodEnum;
import ru.otus.java.spring.project.promotion.domains.providers.ProviderApi;

import java.util.List;
import java.util.Optional;

public interface ProviderApiRepository extends JpaRepository<ProviderApi, Long> {

    List<ProviderApi> findAllByProviderId(long providerId);

    Optional<ProviderApi> findByProviderIdAndBusinessMethod(long providerId, BusinessMethodEnum businessMethod);

}
