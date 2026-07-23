package ru.otus.java.spring.project.promotion.domains.providers;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.otus.java.spring.project.promotion.domains.promotions.BusinessMethodEnum;

@ToString
@Getter
@Setter

@Entity
@Table(name = "provider_apis")
public class ProviderApi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "path", nullable = false)
    private String path;

    @Column(name = "rest_method", length = 6)
    private String restMethod;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "business_method", nullable = false)
    private BusinessMethodEnum businessMethod;

    @Column(name = "description")
    private String description;

    @Column(name = "response_template")
    private String responseTemplate;

    @Column(name = "provider_id", nullable = false)
    private Long providerId;

}