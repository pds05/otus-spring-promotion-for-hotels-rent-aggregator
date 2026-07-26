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
import ru.otus.java.spring.project.promotion.dtos.request.PromoCampaignRqDto;
import ru.otus.java.spring.project.promotion.dtos.response.CampaignHotelParameterDto;
import ru.otus.java.spring.project.promotion.dtos.response.FoodTypeDto;
import ru.otus.java.spring.project.promotion.dtos.response.HotelTypeDto;
import ru.otus.java.spring.project.promotion.dtos.response.PromoCampaignDto;
import ru.otus.java.spring.project.promotion.enums.PromoCampaignStatus;
import ru.otus.java.spring.project.promotion.enums.PromoCampaignType;
import ru.otus.java.spring.project.promotion.services.promotions.PromoCampaignService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = PromoCampaignRestController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class PromoCampaignRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PromoCampaignService promoCampaignService;

    private final List<PromoCampaignDto> promoCampaigns = List.of(
            new PromoCampaignDto(1L, "По минимальной стоимости",
                    PromoCampaignType.LOW_COST.getDescription(),
                    LocalDateTime.of(2027, 1, 1, 0, 0),
                    PromoCampaignStatus.CREATED.getDescription(),
                    null, null, List.of(new PromoCampaignDto.ProviderDto(1L, "Провайдер"),
                    new PromoCampaignDto.ProviderDto(2L, "Провайдер 2")),
                    List.of(new CampaignHotelParameterDto(1L, "Москва", LocalDate.of(2027, 1, 2),
                                    LocalDate.of(2027, 1, 3), 2,
                                    List.of(new HotelTypeDto(1L, "Отель"), new HotelTypeDto(2L, "Квартира")),
                                    Collections.singletonList(new FoodTypeDto(3L, "Без питания"))),
                            new CampaignHotelParameterDto(2L, "Казань", LocalDate.of(2027, 1, 2),
                                    LocalDate.of(2027, 1, 3), 2,
                                    List.of(new HotelTypeDto(1L, "Отель"), new HotelTypeDto(2L, "Квартира")),
                                    Collections.singletonList(new FoodTypeDto(3L, "Без питания"))))),
            new PromoCampaignDto(2L, "С питанием",
                    PromoCampaignType.LOW_COST_WITH_FOOD.getDescription(),
                    LocalDateTime.of(2027, 1, 1, 0, 0),
                    PromoCampaignStatus.READY.getDescription(),
                    null, null, List.of(new PromoCampaignDto.ProviderDto(1L, "Провайдер"), new PromoCampaignDto.ProviderDto(2L, "Провайдер 2")),
                    List.of(new CampaignHotelParameterDto(1L, "Москва", LocalDate.of(2027, 1, 2), LocalDate.of(2027, 1, 3), 2, List.of(new HotelTypeDto(1L, "Отель"), new HotelTypeDto(2L, "Квартира")),
                                    List.of(new FoodTypeDto(1L, "Все включено"), new FoodTypeDto(2L, "Завтрак"))),
                            new CampaignHotelParameterDto(2L, "Казань", LocalDate.of(2027, 1, 2), LocalDate.of(2027, 1, 3), 2, List.of(new HotelTypeDto(1L, "Отель"), new HotelTypeDto(2L, "Квартира")),
                                    List.of(new FoodTypeDto(1L, "Все включено"), new FoodTypeDto(2L, "Завтрак")))))
    );

    @DisplayName("Должен вернуть список промо кампаний")
    @Test
    void shouldReturnPromoCampaigns() throws Exception {
        when(promoCampaignService.getAll()).thenReturn(promoCampaigns);

        mockMvc.perform(get("/api/v1/promo_campaign")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(promoCampaigns)));
    }

    @DisplayName("Должен вернуть промо кампанию по id")
    @Test
    void shouldReturnPromoCampaign() throws Exception {
        when(promoCampaignService.get(1L)).thenReturn(promoCampaigns.get(0));

        mockMvc.perform(get("/api/v1/promo_campaign/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(promoCampaigns.get(0))));
    }

    @DisplayName("Должен запустить промо кампанию")
    @Test
    void shouldStartPromoCampaign() throws Exception {
        when(promoCampaignService.start(2L)).thenReturn(promoCampaigns.get(1));

        mockMvc.perform(put("/api/v1/promo_campaign/start/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(promoCampaigns.get(1))));
    }

    @DisplayName("Должен остановить промо кампанию")
    @Test
    void shouldStopPromoCampaign() throws Exception {
        when(promoCampaignService.stop(2L)).thenReturn(promoCampaigns.get(1));

        mockMvc.perform(put("/api/v1/promo_campaign/stop/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(promoCampaigns.get(1))));
    }

    @DisplayName("Должен создать новую кампанию")
    @Test
    void shouldSavePromoCampaign() throws Exception {
        PromoCampaignRqDto request = createNewPromoCampaign();

        when(promoCampaignService.save(any())).thenReturn(promoCampaigns.get(1));

        mockMvc.perform(post("/api/v1/promo_campaign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(2L));
        verify(promoCampaignService, times(1)).save(request);
    }

    @DisplayName("Должен изменять новую кампанию")
    @Test
    void shouldUpdatePromoCampaign() throws Exception {
        PromoCampaignRqDto request = createNewPromoCampaign();
        request.setId(2L);

        PromoCampaignDto expected = promoCampaigns.get(1);
        expected.setTitle("Новое название");

        when(promoCampaignService.save(any())).thenReturn(expected);

        mockMvc.perform(put("/api/v1/promo_campaign/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(2L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Новое название"));
        verify(promoCampaignService, times(1)).save(request);
    }

    @DisplayName("Должен удалять кампанию")
    @Test
    void shouldDeletePromoCampaign() throws Exception {
        mockMvc.perform(delete("/api/v1/promo_campaign/1", 1))
                .andExpect(status().isNoContent());
        verify(promoCampaignService, times(1)).deleteById(1L);

    }

    private static PromoCampaignRqDto createNewPromoCampaign() {
        PromoCampaignRqDto request = new PromoCampaignRqDto();
        request.setTitle("С питанием");
        request.setStartDate(LocalDateTime.of(2027, 1, 1, 0, 0));
        request.setCampaignType(PromoCampaignType.LOW_COST.getDescription());
        request.setProviderIds(Collections.singletonList(1L));
        request.setHotelParameters(Collections.singletonList(
                new CampaignHotelParameterRqDto(null, null, 1L,
                        LocalDate.of(2027, 1, 2),
                        LocalDate.of(202, 1, 3), 2,
                        List.of(1L, 2L), List.of(1L, 2L)
                )));
        return request;
    }
}
