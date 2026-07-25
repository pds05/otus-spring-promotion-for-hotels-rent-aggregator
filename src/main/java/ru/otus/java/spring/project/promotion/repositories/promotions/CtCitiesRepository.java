package ru.otus.java.spring.project.promotion.repositories.promotions;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.java.spring.project.promotion.domains.promotions.CtCity;

import java.util.Optional;

public interface CtCitiesRepository extends JpaRepository<CtCity, Long> {

    Optional<CtCity> findByTitle(String name);

}
