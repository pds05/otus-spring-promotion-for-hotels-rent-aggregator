package ru.otus.java.spring.project.promotion.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Getter
@Setter
@ConfigurationProperties(prefix = "integration")
public class IntegrationPropertyFileConfig {

    private BaseProperty base;

    private Map<String, IntegrationServiceProperties> providers;

    private TelegramProperty telegram;

    @Getter
    @Setter
    public static class IntegrationServiceProperties {

        private String url;

        @DurationUnit(ChronoUnit.SECONDS)
        private Duration readTimeout;

        @DurationUnit(ChronoUnit.SECONDS)
        private Duration connectTimeout;

        private String topicPrefix;

        private boolean enable = true; //default: service enable can be overridden by property
    }

    @Getter
    @Setter
    public static class BaseProperty {

        private String url;

        private Duration readTimeout;

        private Duration connectTimeout;

        private String topicPrefix;
    }

    @Getter
    @Setter
    public static class TelegramProperty {

        private String url;

        private String chatId;

        private String botUriPrefix;

        private String botApiSendMessage;

        private int messageDelay;

    }
}
