package ru.otus.java.spring.project.promotion.configs;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Objects;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "ru.otus.java.spring.project.promotion.repositories.providers",
        entityManagerFactoryRef = "providersEntityManagerFactory",
        transactionManagerRef = "providersTransactionManager")
public class ProviderDataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.providers")
    public DataSourceProperties providersDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource providersDataSource() {
        return providersDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean providersEntityManagerFactory(
            @Qualifier("providersDataSource") DataSource dataSource,
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(dataSource)
                .packages("ru.otus.java.spring.project.promotion.domains.providers")
                .build();
    }

    @Bean
    public PlatformTransactionManager providersTransactionManager(
            @Qualifier("providersEntityManagerFactory") LocalContainerEntityManagerFactoryBean
                    providersEntityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(providersEntityManagerFactory.getObject()));
    }
}
