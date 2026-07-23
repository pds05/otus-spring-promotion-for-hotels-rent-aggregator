package ru.otus.java.spring.project.promotion;

import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.otus.java.spring.project.promotion.domains.promotions.PromoCampaignType;
import ru.otus.java.spring.project.promotion.dtos.request.CampaignHotelParameterRqDto;
import ru.otus.java.spring.project.promotion.dtos.request.PromoCampaignRqDto;
import ru.otus.java.spring.project.promotion.dtos.response.PromoCampaignDto;
import ru.otus.java.spring.project.promotion.services.promotions.PromoCampaignService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@SpringBootApplication
public class PromotionApplication implements CommandLineRunner {

    private PromoCampaignService promoCampaignService;

    public static void main(String[] args) {
        SpringApplication.run(PromotionApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        PromoCampaignRqDto promoCampaignRqDto = new PromoCampaignRqDto();
        promoCampaignRqDto.setCampaignType(PromoCampaignType.LOW_COST_WITH_FOOD.getDescription());
        promoCampaignRqDto.setTitle("Кампания дешевых номеров");
        promoCampaignRqDto.setProviderIds(List.of(1L, 2L, 3L));
        promoCampaignRqDto.setMessageGroupName("Тестовая группа сообщений");
        promoCampaignRqDto.setStartDate(LocalDateTime.of(2026, 7, 21, 19, 55));

        CampaignHotelParameterRqDto moscow = new CampaignHotelParameterRqDto();
        moscow.setCityName("Москва");
        moscow.setDateIn(LocalDate.of(2027, 1,1));
        moscow.setDateOut(LocalDate.of(2027, 1,2));
        moscow.setGuests(2);
        moscow.setHotelTypeIds(List.of(1L, 3L));
        moscow.setFoodTypeIds(List.of(1L, 2L, 3L, 4L));

        CampaignHotelParameterRqDto kazan = new CampaignHotelParameterRqDto();
        kazan.setCityName("Казань");
        kazan.setDateIn(LocalDate.of(2027, 1,1));
        kazan.setDateOut(LocalDate.of(2027, 1,2));
        kazan.setGuests(2);
        kazan.setHotelTypeIds(Collections.singletonList(3L));
        kazan.setFoodTypeIds(List.of(5L));

        promoCampaignRqDto.setHotelParameters(List.of(moscow, kazan));

        PromoCampaignDto promoCampaign = promoCampaignService.save(promoCampaignRqDto);

        Thread.sleep(5000);

        promoCampaignService.start(promoCampaign.getId());

    }
}
