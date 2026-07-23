package ru.otus.java.spring.project.promotion.repositories.promotions;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.java.spring.project.promotion.domains.promotions.CtFoodType;

import java.util.Optional;

public interface CtHotelFoodTypeRepository extends JpaRepository<CtFoodType, Long> {

    Optional<CtFoodType> findByNameOrDescriptionContainingIgnoreCase(String name, String description);

}
