package ru.otus.java.spring.project.promotion.integrations;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import ru.otus.java.spring.project.promotion.configs.IntegrationPropertyFileConfig;
import ru.otus.java.spring.project.promotion.exceptions.ApplicationException;
import ru.otus.java.spring.project.promotion.exceptions.IntegrationException;

import java.net.URI;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor

@Component("telegramRestClient")
public class TelegramRestClient {

    private final IntegrationPropertyFileConfig integrationPropertyFileConfig;

    @Value("${botToken:none}") // set jvm argument
    private String botToken;

    public void sendMessage(String message) {
        IntegrationPropertyFileConfig.TelegramProperty telegramProperty = integrationPropertyFileConfig.getTelegram();
        RestClient restClient = getRestClient();

        String pathString = "/".concat(telegramProperty.getBotUriPrefix()).concat(botToken)
                .concat("/").concat(telegramProperty.getBotApiSendMessage());
        UriBuilder uriBuilder = UriComponentsBuilder.fromPath(pathString);
        uriBuilder.queryParam("text", message);
        uriBuilder.queryParam("chat_id", telegramProperty.getChatId());
        URI uri = uriBuilder.build();

        log.debug("Sending rest request: uri={}", uri);

        RestClient.ResponseSpec response = restClient.post()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is2xxSuccessful, (req, resp) -> log.debug("Received rest response: uri={}, body={}", uri, resp))
                .onStatus(HttpStatusCode::is4xxClientError, (req, resp) -> {
                    log.warn("Failed request: {}", req);
                    throw new IntegrationException("REQUEST_ERROR", "Failed request, status=" + resp.getStatusCode() + ", message=" + resp.getStatusText());
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, resp) -> {
                    log.warn("Failed request: {}", req);
                    throw new IntegrationException("TELEGRAM_ERROR", "Failed request, status=" + resp.getStatusCode() + ", message=" + resp.getStatusText());
                });

        var resp = response.body(TelegramResponse.class);
        if (!Objects.requireNonNull(resp).isOk()) {
            throw new IntegrationException("RESPONSE_NOK", "Telegram response return false");
        }
    }

    private RestClient getRestClient() {
        if (botToken.equals("none")) {
            log.error("Telegram Bot token is mandatory, set application argument '--botToken={TOKEN}'");
            throw new ApplicationException("Telegram bot configuration error");
        }
        return RestClient.builder()
                .requestFactory(new HttpComponentsClientHttpRequestFactory())
                .baseUrl(integrationPropertyFileConfig.getTelegram().getUrl())
                .build();
    }

    @NoArgsConstructor
    @Getter
    @Setter
    static class TelegramResponse {

        private boolean ok;

    }
}
