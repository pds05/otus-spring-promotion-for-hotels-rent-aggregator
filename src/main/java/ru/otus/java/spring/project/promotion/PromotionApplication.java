package ru.otus.java.spring.project.promotion;

import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.otus.java.spring.project.promotion.enums.PromoCampaignType;
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
public class PromotionApplication implements CommandLineRunner  {

    private PromoCampaignService promoCampaignService;

    public static void main(String[] args) {
        SpringApplication.run(PromotionApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        startLowCostWithFoodCampaign();

        Thread.sleep(5000);

        startLowCostCampaign();
    }

    private void startLowCostWithFoodCampaign() throws InterruptedException {
        PromoCampaignRqDto campaignRqDto = new PromoCampaignRqDto();
        campaignRqDto.setCampaignType(PromoCampaignType.LOW_COST_WITH_FOOD.getDescription());
        campaignRqDto.setTitle("Кампания дешевых номеров с питанием");
        campaignRqDto.setProviderIds(List.of(1L, 2L, 3L));
        campaignRqDto.setStartDate(LocalDateTime.of(2026, 7, 21, 19, 55));

        CampaignHotelParameterRqDto moscow = new CampaignHotelParameterRqDto();
        moscow.setCityId(1L);
        moscow.setDateIn(LocalDate.of(2027, 1,1));
        moscow.setDateOut(LocalDate.of(2027, 1,2));
        moscow.setGuests(2);
        moscow.setHotelTypeIds(List.of(1L, 3L));
        moscow.setFoodTypeIds(List.of(1L));

        CampaignHotelParameterRqDto kazan = new CampaignHotelParameterRqDto();
        kazan.setCityId(3L);
        kazan.setDateIn(LocalDate.of(2027, 1,1));
        kazan.setDateOut(LocalDate.of(2027, 1,2));
        kazan.setGuests(2);
        kazan.setHotelTypeIds(List.of(1L, 2L, 3L));
        kazan.setFoodTypeIds(List.of(1L, 2L, 3L, 4L));

        campaignRqDto.setHotelParameters(List.of(moscow, kazan));

        PromoCampaignDto promoCampaign = promoCampaignService.save(campaignRqDto);

        Thread.sleep(5000);

        promoCampaignService.start(promoCampaign.getId());
    }

    private void startLowCostCampaign() throws InterruptedException {
        PromoCampaignRqDto campaignRqDto = new PromoCampaignRqDto();
        campaignRqDto.setCampaignType(PromoCampaignType.LOW_COST.getDescription());
        campaignRqDto.setTitle("Кампания дешевых номеров");
        campaignRqDto.setProviderIds(Collections.singletonList(1L));
        campaignRqDto.setStartDate(LocalDateTime.of(2026, 7, 21, 19, 56));

        CampaignHotelParameterRqDto campaignParameter = new CampaignHotelParameterRqDto();
        campaignParameter.setCityId(1L);
        campaignParameter.setDateIn(LocalDate.of(2027, 1,1));
        campaignParameter.setDateOut(LocalDate.of(2027, 1,2));
        campaignParameter.setGuests(2);
        campaignParameter.setHotelTypeIds(List.of(1L, 3L));

        campaignRqDto.setHotelParameters(Collections.singletonList(campaignParameter));
        PromoCampaignDto promoCampaign = promoCampaignService.save(campaignRqDto);

        Thread.sleep(5000);

        promoCampaignService.start(promoCampaign.getId());
    }
}
