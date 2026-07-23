package ru.otus.java.spring.project.promotion.repositories.promotions;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.java.spring.project.promotion.domains.promotions.CtCity;

public interface CtCitiesRepository extends JpaRepository<CtCity, Long> {
}
