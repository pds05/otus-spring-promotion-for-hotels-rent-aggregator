package ru.otus.java.spring.project.promotion.controllers.mvc;

import jakarta.websocket.server.PathParam;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.otus.java.spring.project.promotion.dtos.response.CityDto;
import ru.otus.java.spring.project.promotion.dtos.response.FoodTypeDto;
import ru.otus.java.spring.project.promotion.dtos.response.HotelTypeDto;
import ru.otus.java.spring.project.promotion.dtos.response.PromoCampaignDto;
import ru.otus.java.spring.project.promotion.enums.PromoCampaignType;
import ru.otus.java.spring.project.promotion.services.cache.ActiveProviderCache;
import ru.otus.java.spring.project.promotion.services.cache.CitiesCache;
import ru.otus.java.spring.project.promotion.services.cache.FoodTypeCache;
import ru.otus.java.spring.project.promotion.services.cache.HotelTypeCache;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@Controller
@AllArgsConstructor
public class PromoCampaignMvcController {

    private final ActiveProviderCache activeProviderCache;

    private final CitiesCache citiesCache;

    private final FoodTypeCache foodTypeCache;

    private final HotelTypeCache hotelTypeCache;

    private final ModelMapper modelMapper;

    @GetMapping("/promo_campaign")
    public String viewGetAllPromoCampaigns() {
        return "promo_campaigns";
    }

    @GetMapping("/promo_campaign/info")
    public String viewGetPromoCampaign(@PathParam("id") Long id) {
        return "promo_campaign_info";
    }

    @GetMapping("/promo_campaign/config")
    public String viewConfigPromoCampaign(@PathParam("id") Long id, Model model) {
        List<PromoCampaignType> promoCampaignTypes = new ArrayList<>(EnumSet.allOf(PromoCampaignType.class));
        model.addAttribute("campaignTypes", promoCampaignTypes);

        List<PromoCampaignDto.ProviderDto> activeProviders = modelMapper.map(activeProviderCache.getAll(), new TypeToken<List<PromoCampaignDto.ProviderDto>>() {
        }.getType());
        model.addAttribute("activeProviders", activeProviders);

        List<CityDto> cities = modelMapper.map(citiesCache.getAll(), new TypeToken<List<CityDto>>() {
        }.getType());
        model.addAttribute("cities", cities);

        List<FoodTypeDto> foodTypes = modelMapper.map(foodTypeCache.getAll(), new TypeToken<List<FoodTypeDto>>() {
        }.getType());
        model.addAttribute("foodTypes", foodTypes);

        List<HotelTypeDto> hotelTypes = modelMapper.map(hotelTypeCache.getAll(), new TypeToken<List<HotelTypeDto>>() {
        }.getType());
        model.addAttribute("hotelTypes", hotelTypes);

        return "promo_campaign_config";
    }

}
