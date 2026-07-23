package ru.otus.java.spring.project.promotion.domains.promotions;

public enum PromoCampaignResult {

    OK("Успешное выполнение"),
    OK_PARTLY("Выполнена частично"),
    OK_WITH_ERROR("Выполнено с ошибкой"),
    NOK_FAILED("Прервано с ошибкой"),
    INTERRUPTED ("Выполнение остановлено");

    private final String description;

    PromoCampaignResult(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
