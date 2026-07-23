package ru.otus.java.spring.project.promotion.integrations;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import ru.otus.java.spring.project.promotion.domains.providers.Provider;
import ru.otus.java.spring.project.promotion.domains.providers.ProviderApi;
import ru.otus.java.spring.project.promotion.exceptions.ProviderException;
import ru.otus.java.spring.project.promotion.dtos.request.ProviderRequestDto;
import ru.otus.java.spring.project.promotion.services.providers.ProviderService;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@Getter
@Setter
@AllArgsConstructor

@Service(value = "restService")
public class RestClientService {

    private final ObjectMapper objectMapper;

    private final ProviderService activeProviderService;

    public <T> List<T> getResponseCollection(ProviderApi api, ProviderRequestDto request, ParameterizedTypeReference<List<T>> responseType) {
        RestClient.ResponseSpec responseSpec = doGetRequest(api, request);
        return responseSpec.body(responseType);
    }

    private RestClient.ResponseSpec doGetRequest(ProviderApi api, ProviderRequestDto request) {
        if (!api.getRestMethod().equalsIgnoreCase(HttpMethod.GET.name())) {
            throw new ProviderException("REST_METHOD_ERROR", "Rest method " + api.getRestMethod() + " is not supported");
        }

        Provider provider = activeProviderService.getById(api.getProviderId());
        RestClient restClient = getRestClient(provider);
        Map<String, Object> map = objectMapper.convertValue(request, new TypeReference<>() {
        });
        LinkedMultiValueMap<String, String> linkedMultiValueMap = new LinkedMultiValueMap<>();

        map.forEach((k, v) -> {
            if (v instanceof List<?>) {
                ((List<?>) v).forEach(param -> linkedMultiValueMap.add(k, String.valueOf(param)));
            } else {
                linkedMultiValueMap.add(k, String.valueOf(v));
            }
        });

        UriBuilder uriBuilder = UriComponentsBuilder.fromPath(api.getPath());
        URI uri = uriBuilder.queryParams(linkedMultiValueMap).build();

        logRequest(provider, request, uri);

        return restClient.get().uri(uri)
                .accept(APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is2xxSuccessful, (req, resp) -> logResponse(provider, resp, uri))
                .onStatus(HttpStatusCode::is4xxClientError, (req, resp) -> {
                    log.warn("Failed request: {}", req);
                    throw new ProviderException("REQUEST_ERROR", "Failed request, status=" + resp.getStatusCode() + ", message=" + resp.getStatusText());
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, resp) -> {
                    log.warn("Failed request: {}", req);
                    throw new ProviderException("PROVIDER_ERROR", "Failed request, status=" + resp.getStatusCode() + ", message=" + resp.getStatusText());
                });

    }

    private RestClient getRestClient(Provider provider) {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectionRequestTimeout(provider.getConnectTimeout());
        factory.setReadTimeout(provider.getReadTimeout());

        return RestClient.builder()
                .requestFactory(factory)
                .baseUrl(provider.getApiUrl())
                .build();

    }

    private void logRequest(Provider provider, ProviderRequestDto request, URI uri) {
        if (log.isDebugEnabled()) {
            log.trace("Sending rest request: provider={}, uri={}, body={}", provider.getPropertyName(), uri, request);
        } else {
            log.info("Sending rest request: provider={}, uri={}", provider.getPropertyName(), uri);
        }
    }

    private void logResponse(Provider provider, Object response, URI uri) {
        if (log.isDebugEnabled()) {
            log.trace("Received rest response: provider={}, uri={}, body={}", provider.getPropertyName(), uri, response);
        } else {
            log.info("Received rest response: provider={}, uri={}", provider.getPropertyName(), uri);
        }
    }
}
