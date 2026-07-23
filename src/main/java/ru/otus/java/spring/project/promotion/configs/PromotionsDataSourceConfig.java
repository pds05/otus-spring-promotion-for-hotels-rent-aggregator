package ru.otus.java.spring.project.promotion.configs;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Objects;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "ru.otus.java.spring.project.promotion.repositories.promotions",
        entityManagerFactoryRef = "promotionsEntityManagerFactory",
        transactionManagerRef = "promotionsTransactionManager")
public class PromotionsDataSourceConfig {

    @Primary
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.promotions")
    public DataSourceProperties promotionsDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean
    public DataSource promotionsDataSource() {
        return promotionsDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean promotionsEntityManagerFactory(
            @Qualifier("promotionsDataSource") DataSource dataSource,
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(dataSource)
                .packages("ru.otus.java.spring.project.promotion.domains.promotions")
                .build();
    }

    @Primary
    @Bean
    public PlatformTransactionManager promotionsTransactionManager(
            @Qualifier("promotionsEntityManagerFactory") LocalContainerEntityManagerFactoryBean
                    promotionsEntityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(promotionsEntityManagerFactory.getObject()));
    }
}
