package ru.otus.java.spring.project.promotion.tasks;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.otus.java.spring.project.promotion.domains.promotions.*;
import ru.otus.java.spring.project.promotion.domains.providers.Provider;
import ru.otus.java.spring.project.promotion.domains.providers.ProviderApi;
import ru.otus.java.spring.project.promotion.dtos.request.ProviderRequestDto;
import ru.otus.java.spring.project.promotion.dtos.response.HotelRoomsDto;
import ru.otus.java.spring.project.promotion.integrations.RestClientService;
import ru.otus.java.spring.project.promotion.repositories.promotions.PromoCampaignRepository;
import ru.otus.java.spring.project.promotion.services.providers.ActiveProviderService;
import ru.otus.java.spring.project.promotion.services.providers.ProviderService;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Component
@EnableScheduling
public class PromoCampaignExecutor {

    private final PromoCampaignDataHandler promoCampaignDataHandler;

    private final PromoCampaignRepository promoCampaignRepository;

    private final ProviderService activeProviderService;

    private final ProviderService providerService;

    private final RestClientService restService;

    @Scheduled(fixedDelayString = "${executor.delay}")
    public void run() {
        Optional<PromoCampaign> optionalPromoCampaign = promoCampaignRepository.findFirstByStartDateBeforeAndStatus(LocalDateTime.now(), PromoCampaignStatus.READY);

        if (optionalPromoCampaign.isPresent()) {
            PromoCampaign promoCampaign = optionalPromoCampaign.get();
            PromoCampaignData campaignData = new PromoCampaignData(promoCampaign);

            log.debug("Promo campaign starting: {}", promoCampaign);

            promoCampaign.setStatus(PromoCampaignStatus.IN_PROGRESS);
            promoCampaignRepository.save(promoCampaign);

            try {
                Set<CampaignProvider> campaignProviders = promoCampaign.getCampaignProviders();
                List<Provider> activeProviders = activeProviderService.getByIds(campaignProviders.stream()
                        .map(CampaignProvider::getProviderId).toList());

                for (Provider provider : activeProviders) {
                    List<ProviderRequestDto> requestList = promoCampaign.getHotelParameters().stream().map(this::createProviderRequest).toList();
                    doProviderRequest(requestList, provider, campaignData);
                }
                setCompletedStatus(campaignData, campaignProviders);
            } catch (Exception e) {
                promoCampaign.setStatus(PromoCampaignStatus.COMPLETED);
                promoCampaign.setResult(PromoCampaignResult.NOK_FAILED);
                promoCampaignRepository.save(promoCampaign);
                campaignData.addError("Системная ошибка");

                log.error("Promo campaign completed - {}: ", promoCampaign.getResult(), e);
            }
            promoCampaignDataHandler.writeProviderData(campaignData);
            List<ProviderHotelData> targetHotelRooms = promoCampaignDataHandler.getTargetData(campaignData);
            log.info("Target Hotel Rooms: {}", targetHotelRooms);
            promoCampaign.setDetails(String.join(", ", campaignData.getErrors()));
            promoCampaignRepository.save(promoCampaign);
        }
    }

    private void setCompletedStatus(PromoCampaignData campaignData, Set<CampaignProvider> campaignProviders) {
        PromoCampaign promoCampaign = checkResultAndSetStatus(campaignData);
        if (campaignProviders.size() > activeProviderService.getAll().size()) {

            List<Long> disableProviderIds = ((ActiveProviderService) activeProviderService).checkDisableProviders(promoCampaign.getProviderIds());
            providerService.getByIds(disableProviderIds).forEach(p -> campaignData.addError(p.getTitle().concat(" отключен")));
        }
        log.debug("Promo campaign completed - {}: {}", promoCampaign.getResult(), promoCampaign);
    }

    private static PromoCampaign checkResultAndSetStatus(PromoCampaignData campaignData) {
        PromoCampaign promoCampaign = campaignData.getPromoCampaign();
        promoCampaign.setStatus(PromoCampaignStatus.COMPLETED);

        if (campaignData.getSuccessCount() == promoCampaign.getCampaignProviders().size()) {
            promoCampaign.setResult(PromoCampaignResult.OK);
        } else if (campaignData.getSuccessCount() == 0) {
            promoCampaign.setResult(PromoCampaignResult.NOK_FAILED);
        } else if (campaignData.getErrorCount() > 0) {
            promoCampaign.setResult(PromoCampaignResult.OK_WITH_ERROR);
        } else {
            promoCampaign.setResult(PromoCampaignResult.OK_PARTLY);
        }
        return promoCampaign;
    }

    private ProviderRequestDto createProviderRequest(CampaignHotelParameter campaignHotelParameter) {
        ProviderRequestDto request = new ProviderRequestDto();
        request.setCity(campaignHotelParameter.getCityName());
        request.setDateIn(campaignHotelParameter.getDateIn());
        request.setDateOut(campaignHotelParameter.getDateOut());
        request.setHotelTypes(campaignHotelParameter.getCtHotelTypes().stream().map(CtHotelType::getName).toList());
        request.setFoods(campaignHotelParameter.getCtFoodTypes().stream().map(CtFoodType::getName).toList());
        request.setGuests(campaignHotelParameter.getGuests());
        return request;
    }

    private void doProviderRequest(List<ProviderRequestDto> requestList, Provider provider, PromoCampaignData campaignResult) {
        ProviderApi api = provider.getProviderApi(BusinessMethodEnum.FIND_HOTELS_WITH_FILTER);
        try {
            requestList.forEach(req -> {
                var response = restService.getResponseCollection(api, req,
                        new ParameterizedTypeReference<List<HotelRoomsDto>>() {
                        });
                ProviderData responseResult = new ProviderData(provider.getId(), req, response);
                campaignResult.addProviderData(responseResult);
            });
            campaignResult.incSuccess();
        } catch (Exception e) {
            campaignResult.incError();
            campaignResult.addError(provider.getTitle().concat(" не доступен"));
        }
    }
}
