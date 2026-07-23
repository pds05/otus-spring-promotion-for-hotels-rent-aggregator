package ru.otus.java.spring.project.promotion.repositories.providers;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.java.spring.project.promotion.domains.providers.Provider;

import java.util.List;
import java.util.Optional;

public interface ProviderRepository extends JpaRepository<Provider, Long> {

    @EntityGraph(attributePaths = "providerApis")
    List<Provider> findAll();

    @EntityGraph(attributePaths = "providerApis")
    List<Provider> findByIsActiveTrue();

    @EntityGraph(attributePaths = "providerApis")
    List<Provider> findByIdIn(List<Long> ids);

    @EntityGraph(attributePaths = "providerApis")
    Optional<Provider> findByPropertyNameIgnoreCase(String propertyName);

    @EntityGraph(attributePaths = "providerApis")
    Optional<Provider> findById(long id);
}
