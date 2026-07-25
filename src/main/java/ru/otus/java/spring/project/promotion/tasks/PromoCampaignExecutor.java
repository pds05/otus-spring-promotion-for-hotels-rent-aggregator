package ru.otus.java.spring.project.promotion.tasks;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.otus.java.spring.project.promotion.configs.IntegrationPropertyFileConfig;
import ru.otus.java.spring.project.promotion.domains.promotions.*;
import ru.otus.java.spring.project.promotion.domains.providers.Provider;
import ru.otus.java.spring.project.promotion.domains.providers.ProviderApi;
import ru.otus.java.spring.project.promotion.dtos.request.ProviderRequestDto;
import ru.otus.java.spring.project.promotion.dtos.response.HotelRoomsDto;
import ru.otus.java.spring.project.promotion.enums.BusinessMethodEnum;
import ru.otus.java.spring.project.promotion.enums.PromoCampaignResult;
import ru.otus.java.spring.project.promotion.enums.PromoCampaignStatus;
import ru.otus.java.spring.project.promotion.enums.PromoCampaignType;
import ru.otus.java.spring.project.promotion.integrations.ProviderRestClient;
import ru.otus.java.spring.project.promotion.integrations.TelegramRestClient;
import ru.otus.java.spring.project.promotion.repositories.promotions.PromoCampaignRepository;
import ru.otus.java.spring.project.promotion.services.cache.ActiveProviderCache;
import ru.otus.java.spring.project.promotion.services.promotions.ProviderHotelDataServiceImpl;
import ru.otus.java.spring.project.promotion.services.providers.ProviderService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Component
@EnableScheduling
public class PromoCampaignExecutor {

    private final ProviderHotelDataServiceImpl providerHotelDataService;

    private final PromoCampaignRepository promoCampaignRepository;

    private final ActiveProviderCache activeProviderCache;

    private final ProviderService providerService;

    private final ProviderRestClient providerRestClient;

    private final TelegramRestClient telegramRestClient;

    private final IntegrationPropertyFileConfig integrationPropertyFileConfig;

    private static final String MESSAGE_DATE_PATTERN = "dd-MM-yyyy";

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
                List<Provider> activeProviders = activeProviderCache.getByIds(campaignProviders.stream()
                        .map(CampaignProvider::getProviderId).toList());

                for (Provider provider : activeProviders) {
                    List<ProviderRequestDto> requestList = promoCampaign.getHotelParameters().stream().map(this::createProviderRequest).toList();
                    doProviderRequest(requestList, provider, campaignData);
                }

                providerHotelDataService.save(campaignData);

                List<ProviderHotelData> targetHotelData = providerHotelDataService.parseTop(promoCampaign);
                campaignData.addTargetProviderHotel(targetHotelData);
                log.debug("Target hotel rooms: {}", targetHotelData);

                doSendTelegramMessage(campaignData);

                setCompletedStatus(campaignData, campaignProviders);
            } catch (Exception e) {
                promoCampaign.setStatus(PromoCampaignStatus.COMPLETED);
                promoCampaign.setResult(PromoCampaignResult.NOK_FAILED);
                campaignData.addErrorMessage("Системная ошибка");

                log.error("Promo campaign completed - {}: ", promoCampaign.getResult(), e);
            }

            promoCampaign.setDetails(String.join(", ", campaignData.getErrorMessages()));
            promoCampaignRepository.save(promoCampaign);
        }
    }

    private void doSendTelegramMessage(PromoCampaignData campaignData) {
        PromoCampaignType promoCampaignType = campaignData.getPromoCampaign().getCampaignType();

        List<ProviderHotelData> targetProviderHotels = campaignData.getTargetProviderHotels();
        targetProviderHotels.forEach(hotelRoom -> {
            try {
                switch (promoCampaignType) {
                    case LOW_COST -> {
                        String message = String.format(promoCampaignType.getTelegramMessageTemplate(),
                                hotelRoom.getCityName(),
                                hotelRoom.getHotelName(),
                                hotelRoom.getHotelRoomName(),
                                hotelRoom.getMaxGuests(),
                                hotelRoom.getPrice(),
                                hotelRoom.getDateIn().format(DateTimeFormatter.ofPattern(MESSAGE_DATE_PATTERN)));
                        telegramRestClient.sendMessage(message);
                    }
                    case LOW_COST_WITH_FOOD -> {
                        String message = String.format(promoCampaignType.getTelegramMessageTemplate(),
                                hotelRoom.getCityName(),
                                hotelRoom.getFood().toLowerCase(),
                                hotelRoom.getHotelName(),
                                hotelRoom.getHotelRoomName(),
                                hotelRoom.getMaxGuests(),
                                hotelRoom.getPrice(),
                                hotelRoom.getDateIn().format(DateTimeFormatter.ofPattern(MESSAGE_DATE_PATTERN)));
                        telegramRestClient.sendMessage(message);
                    } default -> {
                        campaignData.incError();
                        campaignData.addErrorMessage("Не нашлось сообщения для Telegram");
                        log.warn("Impossible to prepare telegram message, promo campaign type is not defined");
                    }
                }
                if (targetProviderHotels.size() > 1) {
                    try {
                        Thread.sleep(integrationPropertyFileConfig.getTelegram().getMessageDelay());
                    } catch (InterruptedException e) {
                        campaignData.incError();
                        campaignData.addErrorMessage("Не удалось отправить сообщение в Telegram");
                        log.error("Application error while sending telegram messages", e);
                    }
                }
            } catch (Exception e) {
                campaignData.incError();
                campaignData.addErrorMessage("Telegram не доступен");
                log.error("Failed to send telegram message", e);
            }
        });
    }

    private void setCompletedStatus(PromoCampaignData campaignData, Set<CampaignProvider> campaignProviders) {
        PromoCampaign promoCampaign = checkResultAndSetStatus(campaignData);
        if (campaignProviders.size() > activeProviderCache.size()) {

            List<Long> disableProviderIds = activeProviderCache.checkDisableProviders(promoCampaign.getProviderIds());
            providerService.getByIds(disableProviderIds).forEach(p -> campaignData.addErrorMessage(p.getTitle().concat(" отключен")));
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
        request.setCity(campaignHotelParameter.getCity().getTitle());
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
                var response = providerRestClient.sendMessage(api, req,
                        new ParameterizedTypeReference<List<HotelRoomsDto>>() {
                        });
                ProviderData responseResult = new ProviderData(provider.getId(), req, response);
                campaignResult.addProviderData(responseResult);
            });
            campaignResult.incSuccess();
        } catch (Exception e) {
            campaignResult.incError();
            campaignResult.addErrorMessage(provider.getTitle().concat(" не доступен"));
        }
    }
}
