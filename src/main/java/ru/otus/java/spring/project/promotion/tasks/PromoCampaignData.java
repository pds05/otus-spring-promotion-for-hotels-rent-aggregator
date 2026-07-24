package ru.otus.java.spring.project.promotion.tasks;

import lombok.Getter;
import lombok.Setter;
import ru.otus.java.spring.project.promotion.domains.promotions.PromoCampaign;
import ru.otus.java.spring.project.promotion.domains.promotions.ProviderHotelData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class PromoCampaignData {

    private PromoCampaign promoCampaign;

    public PromoCampaignData(PromoCampaign promoCampaign) {
        this.promoCampaign = promoCampaign;
    }

    private int successCount = 0;

    private int errorCount = 0;

    private final Map<Long, List<ProviderData>> providerDataMultiMap = new HashMap<>();

    private final List<ProviderHotelData> targetProviderHotels = new ArrayList<>();

    private final List<String> errorMessages = new ArrayList<>();

    public List<ProviderData> getProviderData(long providerId) {
        return providerDataMultiMap.get(providerId);
    }

    public void addProviderData(ProviderData result) {
        providerDataMultiMap.computeIfAbsent(result.getProviderId(), k -> new ArrayList<>()).add(result);
    }

    public void addErrorMessage(String message) {
        errorMessages.add(message);
    }

    public void incError() {
        errorCount++;
    }

    public void incSuccess() {
        successCount++;
    }

    public void addTargetProviderHotel(List<ProviderHotelData> providerHotel) {
        targetProviderHotels.addAll(providerHotel);
    }

    public void addTargetProviderHotel(ProviderHotelData providerHotel) {
        targetProviderHotels.add(providerHotel);
    }
}
