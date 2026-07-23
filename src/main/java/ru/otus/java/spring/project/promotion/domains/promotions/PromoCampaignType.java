package ru.otus.java.spring.project.promotion.domains.promotions;

import lombok.Getter;
import ru.otus.java.spring.project.promotion.exceptions.ApplicationException;

import java.util.Arrays;

@Getter
public enum PromoCampaignType {

    LOW_COST("Самый дешевый номер"), LOW_COST_WITH_FOOD("Самый дешевый номер с питанием");

    private final String description;

    PromoCampaignType(String description) {
        this.description = description;
    }

    public static PromoCampaignType getPromoCampaignType(String description) {
        return Arrays.stream(PromoCampaignType.values())
                .filter(type -> type.getDescription().equals(description))
                .findFirst()
                .orElseThrow(() -> new ApplicationException("Unknown promo campaign type " + description));
    }
}
