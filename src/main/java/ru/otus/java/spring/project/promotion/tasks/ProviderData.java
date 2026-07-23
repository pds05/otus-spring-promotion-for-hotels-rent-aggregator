package ru.otus.java.spring.project.promotion.tasks;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.otus.java.spring.project.promotion.dtos.request.ProviderRequestDto;
import ru.otus.java.spring.project.promotion.dtos.response.HotelRoomsDto;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor

public class ProviderData {

    private long providerId;

    private ProviderRequestDto request;

    private List<HotelRoomsDto> responseList;

}