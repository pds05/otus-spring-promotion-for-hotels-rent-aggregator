package ru.otus.java.spring.project.promotion.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.java.spring.project.promotion.dtos.response.ProviderDto;
import ru.otus.java.spring.project.promotion.services.cache.ActiveProviderCache;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ProviderRestController {

    private final ActiveProviderCache activeProviderCache;

    private final ModelMapper modelMapper;

    @GetMapping(value = "/api/v1/provider", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    private List<ProviderDto> getAllProviders() {
        return modelMapper.map(activeProviderCache.getAll(), new TypeToken<List<ProviderDto>>(){}.getType());
    }

    @GetMapping(value = "/api/v1/provider/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    private ProviderDto getProviderById(@PathVariable Long id) {
        return modelMapper.map(activeProviderCache.get(id), ProviderDto.class);
    }

}
