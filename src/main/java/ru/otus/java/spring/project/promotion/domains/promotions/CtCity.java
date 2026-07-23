package ru.otus.java.spring.project.promotion.domains.promotions;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ct_cities")
public class CtCity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "phone_prefix", length = 6)
    private String phonePrefix;

    @NotEmpty
    @Column(name = "title", length = 50)
    private String title;

}