package ru.otus.java.spring.project.promotion.repositories.promotions;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.java.spring.project.promotion.domains.promotions.User;

public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph("user-authorities-entity-graph")
    User findByUsername(String username);
}
