package ru.otus.java.spring.project.promotion.controllers.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.otus.java.spring.project.promotion.dtos.request.CampaignHotelParameterRqDto;
import ru.otus.java.spring.project.promotion.dtos.response.CampaignHotelParameterDto;
import ru.otus.java.spring.project.promotion.dtos.response.FoodTypeDto;
import ru.otus.java.spring.project.promotion.dtos.response.HotelTypeDto;
import ru.otus.java.spring.project.promotion.services.promotions.CampaignHotelParameterService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CampaignHotelParameterRestController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class CampaignHotelParameterRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CampaignHotelParameterService campaignHotelParameterService;

    private final List<CampaignHotelParameterDto> hotelParameters =  List.of(new CampaignHotelParameterDto(1L, "Москва", LocalDate.of(2027, 1, 2),
                    LocalDate.of(2027, 1, 3), 2,
                    List.of(new HotelTypeDto(1L, "Отель"), new HotelTypeDto(2L, "Квартира")),
                    Collections.singletonList(new FoodTypeDto(3L, "Без питания"))),
            new CampaignHotelParameterDto(2L, "Казань", LocalDate.of(2027, 1, 2),
                    LocalDate.of(2027, 1, 3), 2,
                    List.of(new HotelTypeDto(1L, "Отель"), new HotelTypeDto(2L, "Квартира")),
                    Collections.singletonList(new FoodTypeDto(3L, "Без питания"))));

    @DisplayName("Должен вернуть список параметров кампании")
    @Test
    void shouldReturnParametersByCampaignId() throws Exception {
        when(campaignHotelParameterService.getAllByCampaignId(1L)).thenReturn(hotelParameters);

        mockMvc.perform(get("/api/v1/campaign_hotel_parameter")
                        .queryParam("campaignId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(hotelParameters)));
    }

    @DisplayName("Должен вернуть параметр по id")
    @Test
    void shouldReturnParameterById() throws Exception {
        when(campaignHotelParameterService.getById(1L)).thenReturn(hotelParameters.get(0));

        mockMvc.perform(get("/api/v1/campaign_hotel_parameter/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(hotelParameters.get(0))));
    }

    @DisplayName("Должен сохранить новый параметр кампании")
    @Test
    void shouldSaveCampaignParameter() throws Exception {
        CampaignHotelParameterRqDto request = new CampaignHotelParameterRqDto(null, 1L, 1L,
                LocalDate.of(2027, 1, 2), LocalDate.of(2027, 1, 3), 2, List.of(1L, 2L),
                Collections.singletonList(3L));

        CampaignHotelParameterDto expected = new CampaignHotelParameterDto(3L, "Казань",
                LocalDate.of(2027, 1, 2), LocalDate.of(2027, 1, 3), 2,
                List.of(new HotelTypeDto(1L, "Отель"), new HotelTypeDto(2L, "Квартира")),
                Collections.singletonList(new FoodTypeDto(3L, "Без питания")));

        when(campaignHotelParameterService.save(request)).thenReturn(expected);

        mockMvc.perform(post("/api/v1/campaign_hotel_parameter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(3L));
        verify(campaignHotelParameterService, times(1)).save(request);
    }

    @DisplayName("Должен изменять параметр кампании")
    @Test
    void shouldUpdateCampaignParameter() throws Exception {
        CampaignHotelParameterRqDto request = new CampaignHotelParameterRqDto(2L, 1L, 3L,
                LocalDate.of(2027, 1, 2),
                LocalDate.of(2027, 1, 3), 2, List.of(1L, 2L),
                Collections.singletonList(3L));

        CampaignHotelParameterDto expected = new CampaignHotelParameterDto(2L, "Казань", LocalDate.of(2027, 1, 2),
                LocalDate.of(2027, 1, 3), 2,
                List.of(new HotelTypeDto(1L, "Отель"), new HotelTypeDto(2L, "Квартира")),
                Collections.singletonList(new FoodTypeDto(3L, "Без питания")));

        when(campaignHotelParameterService.save(request)).thenReturn(expected);

        mockMvc.perform(put("/api/v1/campaign_hotel_parameter/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(2L));
        verify(campaignHotelParameterService, times(1)).save(request);
    }

    @DisplayName("Должен удалять параметр кампании")
    @Test
    void shouldDeletePromoCampaign() throws Exception {
        mockMvc.perform(delete("/api/v1/campaign_hotel_parameter/1", 1))
                .andExpect(status().isNoContent());
        verify(campaignHotelParameterService, times(1)).deleteById(1L);

    }
}
