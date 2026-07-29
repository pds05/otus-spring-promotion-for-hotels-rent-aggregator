package ru.otus.java.spring.project.promotion.configs;

import lombok.AllArgsConstructor;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.java.spring.project.promotion.domains.providers.Provider;
import ru.otus.java.spring.project.promotion.dtos.response.*;
import ru.otus.java.spring.project.promotion.services.cache.CitiesCache;
import ru.otus.java.spring.project.promotion.services.cache.FoodTypeCache;
import ru.otus.java.spring.project.promotion.services.cache.HotelTypeCache;
import ru.otus.java.spring.project.promotion.domains.promotions.*;
import ru.otus.java.spring.project.promotion.dtos.request.CampaignHotelParameterRqDto;
import ru.otus.java.spring.project.promotion.dtos.request.PromoCampaignRqDto;
import ru.otus.java.spring.project.promotion.enums.PromoCampaignResult;
import ru.otus.java.spring.project.promotion.enums.PromoCampaignStatus;
import ru.otus.java.spring.project.promotion.enums.PromoCampaignType;
import ru.otus.java.spring.project.promotion.services.providers.ProviderService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.modelmapper.config.Configuration.AccessLevel.PRIVATE;

@AllArgsConstructor
@Configuration
public class ModelMapperConfig {

    private ProviderService providerService;

    private HotelTypeCache hotelTypeCache;

    private FoodTypeCache foodTypeCache;

    private CitiesCache citiesCache;

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
        mappingCampaignHotelParameterToDto(mapper);
        mappingCampaignHotelParameterDtoToDomain(mapper);
        mappingProviderHotelDataToDto(mapper);
        mappingProviderDomainToDto(mapper);

        return mapper;
    }

    private void mappingCtHotelTypeToDto(ModelMapper mapper) {
        mapper.createTypeMap(CtHotelType.class, HotelTypeDto.class)
                .addMapping(CtHotelType::getDescription, HotelTypeDto::setType)
                .addMapping(CtHotelType::getId, HotelTypeDto::setId);
    }

    private void mappingCtFoodTypeToDto(ModelMapper mapper) {
        mapper.createTypeMap(CtFoodType.class, FoodTypeDto.class)
                .addMapping(CtFoodType::getDescription, FoodTypeDto::setType)
                .addMapping(CtFoodType::getId, FoodTypeDto::setId);
    }

    private void mappingPromoCampaignToDto(ModelMapper mapper) {
        Converter<Set<CampaignProvider>, List<PromoCampaignDto.ProviderDto>> providerToDtoConverter = mc -> mc.getSource().stream()
                .map(provider -> {
                    long providerId = provider.getProviderId();
                    String providerName = providerService.getById(providerId).getDescription();
                    return new PromoCampaignDto.ProviderDto(providerId, providerName);
                }).toList();

        Converter<PromoCampaignType, String> campaignTypeStringConverter = mc -> mc.getSource().getDescription();

        Converter<PromoCampaignStatus, String> campaignStatusStringConverter = mc -> mc.getSource().getDescription();

        Converter<PromoCampaignResult, String> campaignResultConverter = mc -> {
            var result = mc.getSource();
            return result == null ? null : result.getDescription();
        };

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

    private void mappingProviderDomainToDto(ModelMapper mapper) {
        mapper.createTypeMap(Provider.class, PromoCampaignDto.ProviderDto.class)
                .addMapping(Provider::getId, PromoCampaignDto.ProviderDto::setId)
                .addMapping(Provider::getDescription,PromoCampaignDto.ProviderDto::setName);
    }

    private void mappingCampaignRqDtoToDomain(ModelMapper mapper) {
        Converter<String, PromoCampaignType> stringPromoCampaignTypeConverter = mc -> PromoCampaignType.valueOf(mc.getSource());

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
                    return new CampaignHotelParameter(req.getId(), city,req.getCampaignId(), req.getDateIn(), req.getDateOut(), req.getGuests(), hotelTypes, foodTypes);
                }).collect(Collectors.toSet());

        Converter<List<Long>, Set<CampaignProvider>> campaignProviderIdToModelListConverter = m ->
                m.getSource().stream().map(id -> new CampaignProvider(null, id)).collect(Collectors.toSet());

        mapper.createTypeMap(PromoCampaignRqDto.class, PromoCampaign.class)
                .addMapping(PromoCampaignRqDto::getId, PromoCampaign::setId)
                .addMappings(m -> m.using(stringPromoCampaignTypeConverter)
                        .map(PromoCampaignRqDto::getCampaignType, PromoCampaign::setCampaignType))
                .addMappings(mc -> mc.using(hotelParametersDtoToDomainConverter)
                        .map(PromoCampaignRqDto::getHotelParameters, PromoCampaign::setHotelParameters))
                .addMappings(mc -> mc.using(campaignProviderIdToModelListConverter)
                        .map(PromoCampaignRqDto::getProviderIds, PromoCampaign::setCampaignProviders))
        ;
    }

    private void mappingCampaignHotelParameterToDto(ModelMapper mapper) {
        Converter<Set<CtFoodType>, List<FoodTypeDto>> foodConverter = mc ->
                mc.getSource().stream().map(domain -> new FoodTypeDto(domain.getId(), domain.getDescription())).toList();

        Converter<Set<CtHotelType>, List<HotelTypeDto>> hotelTypeConverter = mc ->
                mc.getSource().stream().map(domain -> new HotelTypeDto(domain.getId(), domain.getDescription())).toList();

        mapper.createTypeMap(CampaignHotelParameter.class, CampaignHotelParameterDto.class)
                .addMapping(domain -> domain.getCity().getTitle(), CampaignHotelParameterDto::setCityName)
                .addMappings(mc -> mc.using(hotelTypeConverter)
                        .map(CampaignHotelParameter::getCtHotelTypes, CampaignHotelParameterDto::setHotelTypes))
                .addMappings(mc -> mc.using(foodConverter)
                        .map(CampaignHotelParameter::getCtFoodTypes, CampaignHotelParameterDto::setFoodTypes));
    }

    private void mappingCampaignHotelParameterDtoToDomain(ModelMapper mapper) {
        Converter<List<Long>, Set<CtHotelType>> hotelTypeConverter = mc ->
                new HashSet<>(hotelTypeCache.getByIds(mc.getSource()));

        Converter<List<Long>, Set<CtFoodType>> foodTypeConverter = mc ->
                new HashSet<>(foodTypeCache.getByIds(mc.getSource()));

        Converter<Long, CtCity> cityConverter = mc -> citiesCache.get(mc.getSource());

        mapper.createTypeMap(CampaignHotelParameterRqDto.class, CampaignHotelParameter.class)
                .addMapping(CampaignHotelParameterRqDto::getCampaignId, CampaignHotelParameter::setCampaignId)
                .addMappings(mc -> mc.using(cityConverter)
                        .map(CampaignHotelParameterRqDto::getCityId, CampaignHotelParameter::setCity))
                .addMappings(mc -> mc.using(hotelTypeConverter)
                        .map(CampaignHotelParameterRqDto::getHotelTypeIds, CampaignHotelParameter::setCtHotelTypes))
                .addMappings(mc -> mc.using(foodTypeConverter)
                        .map(CampaignHotelParameterRqDto::getFoodTypeIds, CampaignHotelParameter::setCtFoodTypes));
    }

    private void mappingProviderHotelDataToDto(ModelMapper mapper) {
        Converter<Long, String> providerConverter = mc ->  {
            var provider = providerService.getById(mc.getSource());
            return provider != null ? provider.getDescription() : null;
        };

        mapper.createTypeMap(ProviderHotelData.class, ProviderHotelDataDto.class)
                .addMapping(data -> data.getPromoCampaign().getId(), ProviderHotelDataDto::setPromoCampaignId)
                .addMapping(ProviderHotelData::getCityName, ProviderHotelDataDto::setCity)
                .addMapping(ProviderHotelData::getHotelName, ProviderHotelDataDto::setHotel)
                .addMapping(ProviderHotelData::getHotelRoomName, ProviderHotelDataDto::setRoom)
                .addMapping(ProviderHotelData::getHotelRoomRateName, ProviderHotelDataDto::setRate)
                .addMapping(ProviderHotelData::getDateCreate, ProviderHotelDataDto::setActualDate)
                .addMappings(mc -> mc.using(providerConverter)
                        .map(ProviderHotelData::getProviderId, ProviderHotelDataDto::setProvider));
    }
}
