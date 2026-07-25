package ru.otus.java.spring.project.promotion.configs;

import lombok.RequiredArgsConstructor;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.java.spring.project.promotion.services.cache.CitiesCache;
import ru.otus.java.spring.project.promotion.services.cache.FoodTypeCache;
import ru.otus.java.spring.project.promotion.services.cache.HotelTypeCache;
import ru.otus.java.spring.project.promotion.domains.promotions.*;
import ru.otus.java.spring.project.promotion.dtos.request.CampaignHotelParameterRqDto;
import ru.otus.java.spring.project.promotion.dtos.request.PromoCampaignRqDto;
import ru.otus.java.spring.project.promotion.dtos.response.CampaignHotelParameterDto;
import ru.otus.java.spring.project.promotion.dtos.response.FoodTypeDto;
import ru.otus.java.spring.project.promotion.dtos.response.HotelTypeDto;
import ru.otus.java.spring.project.promotion.dtos.response.PromoCampaignDto;
import ru.otus.java.spring.project.promotion.enums.PromoCampaignResult;
import ru.otus.java.spring.project.promotion.enums.PromoCampaignStatus;
import ru.otus.java.spring.project.promotion.enums.PromoCampaignType;
import ru.otus.java.spring.project.promotion.services.providers.ProviderService;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.modelmapper.config.Configuration.AccessLevel.PRIVATE;

@RequiredArgsConstructor
@Configuration
public class ModelMapperConfig {

    private final ProviderService providerService;

    private final HotelTypeCache hotelTypeCache;

    private final FoodTypeCache foodTypeCache;

    private final CitiesCache citiesCache;

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        mapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setSkipNullEnabled(true)
                .setFieldAccessLevel(PRIVATE);
        mappingPromoCampaignToDto(mapper);
        mappingCampaignRqDtoToDomain(mapper);
        mappingCtHotelTypeToDto(mapper);
        mappingCtFoodTypeToDto(mapper);

        return mapper;
    }

    private void mappingCtHotelTypeToDto(ModelMapper mapper) {
        mapper.createTypeMap(CtHotelType.class, HotelTypeDto.class)
                .addMapping(CtHotelType::getName, HotelTypeDto::setType);
    }

    private void mappingCtFoodTypeToDto(ModelMapper mapper) {
        mapper.createTypeMap(CtFoodType.class, FoodTypeDto.class)
                .addMapping(CtFoodType::getDescription, FoodTypeDto::setType);
    }

    private void mappingPromoCampaignToDto(ModelMapper mapper) {
        Converter<Set<CampaignProvider>, List<PromoCampaignDto.ProviderDto>> providerToDtoConverter = mc -> mc.getSource().stream()
                .map(provider -> {
                    long providerId = provider.getProviderId();
                    String providerName = providerService.getById(providerId).getTitle();
                    return new PromoCampaignDto.ProviderDto(providerId, providerName);
                }).toList();

        Converter<PromoCampaignType, String> campaignTypeStringConverter = mc -> mc.getSource().getDescription();

        Converter<PromoCampaignStatus, String> campaignStatusStringConverter = mc -> mc.getSource().getDescription();

        Converter<PromoCampaignResult, String> campaignResultConverter = mc -> Optional.ofNullable(mc.getSource()).orElse(PromoCampaignResult.NOK_FAILED).getDescription();

        Converter<Set<CampaignHotelParameter>, List<CampaignHotelParameterDto>> parameterConverter = mc ->
                mc.getSource().stream().map(parameter -> new CampaignHotelParameterDto(parameter.getId(),
                        parameter.getCity().getTitle(),
                        parameter.getDateIn(),
                        parameter.getDateOut(),
                        parameter.getGuests(),
                        parameter.getCtHotelTypes() == null ? null : parameter.getCtHotelTypes().stream().map(ctHotelType ->
                                new HotelTypeDto(ctHotelType.getId(), ctHotelType.getDescription())).toList(),
                        parameter.getCtFoodTypes() == null ? null : parameter.getCtFoodTypes().stream().map(ctFoodType ->
                                new FoodTypeDto(ctFoodType.getId(), ctFoodType.getDescription())).toList()
                )).toList();

        mapper.createTypeMap(PromoCampaign.class, PromoCampaignDto.class)
                .addMappings(m -> m.using(campaignTypeStringConverter)
                        .map(PromoCampaign::getCampaignType, PromoCampaignDto::setCampaignType))
                .addMappings(m -> m.using(campaignStatusStringConverter)
                        .map(PromoCampaign::getStatus, PromoCampaignDto::setStatus))
                .addMappings(m -> m.using(campaignResultConverter)
                        .map(PromoCampaign::getResult, PromoCampaignDto::setResult))
                .addMappings(m -> m.using(providerToDtoConverter)
                        .map(PromoCampaign::getCampaignProviders, PromoCampaignDto::setProviders))
                .addMappings(m -> m.using(parameterConverter)
                        .map(PromoCampaign::getHotelParameters, PromoCampaignDto::setHotelParameters));
    }

    private void mappingCampaignRqDtoToDomain(ModelMapper mapper) {
        Converter<String, PromoCampaignType> stringPromoCampaignTypeConverter = mc -> PromoCampaignType.getPromoCampaignType(mc.getSource());

        Converter<List<CampaignHotelParameterRqDto>, Set<CampaignHotelParameter>> hotelParametersDtoToDomainConverter = m -> m.getSource().stream()
                .map(req -> {
                    Set<CtHotelType> hotelTypes = null;
                    if (req.getHotelTypeIds() != null) {
                        hotelTypes = new HashSet<>(hotelTypeCache.getByIds(req.getHotelTypeIds()));
                    }
                    Set<CtFoodType> foodTypes = null;
                    if (req.getFoodTypeIds() != null) {
                        foodTypes = new HashSet<>(foodTypeCache.getByIds(req.getFoodTypeIds()));
                    }
                    CtCity city = citiesCache.get(req.getCityId());
                    return new CampaignHotelParameter(req.getId(), city, req.getDateIn(), req.getDateOut(), req.getGuests(), hotelTypes, foodTypes);
                }).collect(Collectors.toSet());

        Converter<List<Long>, Set<CampaignProvider>> campaignProviderIdToModelListConverter = m ->
                m.getSource().stream().map(id -> new CampaignProvider(null, id)).collect(Collectors.toSet());

        mapper.createTypeMap(PromoCampaignRqDto.class, PromoCampaign.class)
                .addMappings(m -> m.using(stringPromoCampaignTypeConverter)
                        .map(PromoCampaignRqDto::getCampaignType, PromoCampaign::setCampaignType))
                .addMappings(mc -> mc.using(hotelParametersDtoToDomainConverter)
                        .map(PromoCampaignRqDto::getHotelParameters, PromoCampaign::setHotelParameters))
                .addMappings(mc -> mc.using(campaignProviderIdToModelListConverter)
                        .map(PromoCampaignRqDto::getProviderIds, PromoCampaign::setCampaignProviders));
    }
}
