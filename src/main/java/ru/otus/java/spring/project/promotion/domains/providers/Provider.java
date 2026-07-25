package ru.otus.java.spring.project.promotion.domains.providers;

import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.*;
import ru.otus.java.spring.project.promotion.enums.BusinessMethodEnum;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.hibernate.type.SqlTypes.INTERVAL_SECOND;

@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

@Entity
@Table(name = "providers")
public class Provider {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "providers_id_gen")
    @SequenceGenerator(name = "providers_id_gen", sequenceName = "providers_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotEmpty
    @Column(name = "title", nullable = false, length = 50)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "property_name")
    private String propertyName;

    @NotNull
    @Column(name = "api_url", nullable = false)
    private String apiUrl;

    @Column(name = "api_login", length = 50)
    private String apiLogin;

    @Column(name = "api_password")
    private String apiPassword;

    @ColumnDefault("true")
    @Column(name = "is_active")
    private Boolean isActive;

    @JdbcTypeCode(INTERVAL_SECOND)
    @Column(name = "read_timeout")
    private Duration readTimeout;

    @JdbcTypeCode(INTERVAL_SECOND)
    @Column(name = "connect_timeout")
    private Duration connectTimeout;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE, orphanRemoval = true)
    @JoinColumn(name = "provider_id")
    private List<ProviderApi> providerApis;

    public ProviderApi getProviderApi(BusinessMethodEnum businessMethod) {
        return providerApis.stream().filter(api -> api.getBusinessMethod().equals(businessMethod)).findFirst().orElse(null);
    }

}