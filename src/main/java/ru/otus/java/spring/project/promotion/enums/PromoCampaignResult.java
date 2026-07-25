package ru.otus.java.spring.project.promotion.enums;

import lombok.Getter;

@Getter
public enum PromoCampaignResult {

    OK("Успешное выполнение"),
    OK_PARTLY("Выполнена частично"),
    OK_WITH_ERROR("Выполнена с ошибкой"),
    NOK_FAILED("Не выполнена с ошибкой"),
    INTERRUPTED ("Выполнение остановлено");

    private final String description;

    PromoCampaignResult(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
