package ru.otus.java.spring.project.promotion.enums;

import lombok.Getter;
import ru.otus.java.spring.project.promotion.exceptions.ApplicationException;

import java.util.Arrays;

@Getter
public enum PromoCampaignType {

    LOW_COST("Самый дешевый номер",
            "Самый дешевый номер в г. %s: гостиница '%s' номер '%s' на %d человек за %,.2f рублей в сутки, заезд %s"),
    LOW_COST_WITH_FOOD("Самый дешевый номер с питанием",
            "Самый дешевый номер в г. %s с питанием %s: гостиница '%s' номер '%s' на %d человек за %,.2f рублей в сутки, заезд %s");

    private final String description;

    private final String telegramMessageTemplate;

    PromoCampaignType(String description, String telegramMessageTemplate) {
        this.description = description;
        this.telegramMessageTemplate = telegramMessageTemplate;
    }

    public static PromoCampaignType getPromoCampaignType(String name) {
        return Arrays.stream(PromoCampaignType.values())
                .filter(type -> type.name().equals(name))
                .findFirst()
                .orElseThrow(() -> new ApplicationException("Unknown promo campaign type " + name));
    }
}
