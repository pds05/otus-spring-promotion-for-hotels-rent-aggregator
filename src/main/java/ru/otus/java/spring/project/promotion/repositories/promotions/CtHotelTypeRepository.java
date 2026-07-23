package ru.otus.java.spring.project.promotion.repositories.promotions;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.java.spring.project.promotion.domains.promotions.CtHotelType;

import java.util.Optional;

public interface CtHotelTypeRepository extends JpaRepository<CtHotelType, Long> {

    Optional<CtHotelType> findByNameOrDescriptionContainingIgnoreCase(String name, String description);

}
