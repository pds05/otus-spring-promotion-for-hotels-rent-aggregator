package ru.otus.java.spring.project.promotion.domains.promotions;

public enum PromoCampaignStatus {

    CREATED("Создана"),
    READY("Запущена"),
    IN_PROGRESS("Выполняется"),
    COMPLETED("Завершена"),
    IDLE("Остановлена");

    private final String description;

    PromoCampaignStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}