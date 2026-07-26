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
import ru.otus.java.spring.project.promotion.dtos.response.ProviderHotelDataDto;
import ru.otus.java.spring.project.promotion.services.promotions.ProviderHotelDataService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProviderHotelDataRestController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class ProviderHotelDataRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProviderHotelDataService providerHotelDataService;

    private final List<ProviderHotelDataDto> tops = List.of(
            new ProviderHotelDataDto(1L, "Провайдер", "Москва", "Гостиница", "Номер", "Тариф", "1000", 2, "Питание", LocalDate.of(2027,1,2), LocalDate.of(2027,1,3), LocalDateTime.of(2027,1,1, 12, 0, 0)),
            new ProviderHotelDataDto(1L, "Провайдер", "Казань", "Гостиница", "Номер", "Тариф", "1000", 2, "Без питания", LocalDate.of(2027,1,2), LocalDate.of(2027,1,3), LocalDateTime.of(2027,1,1, 12, 0, 0)));

    @DisplayName("Должен вернуть топ список рассылки")
    @Test
    void shouldReturnTopData() throws Exception {
        when(providerHotelDataService.getTop(1L)).thenReturn(tops);

        mockMvc.perform(get("/api/v1/top_data")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("campaignId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(tops)));
    }

    @DisplayName("Должен удалять топ рассылки по id кампании")
    @Test
    void shouldDeleteTop() throws Exception {
        mockMvc.perform(delete("/api/v1/top_data")
                        .contentType(MediaType.APPLICATION_JSON)
                .queryParam("campaignId", "1"))
                .andExpect(status().isNoContent());
        verify(providerHotelDataService, times(1)).deleteByCampaignId(1L);
    }


}
