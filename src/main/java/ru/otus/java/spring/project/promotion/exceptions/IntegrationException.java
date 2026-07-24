package ru.otus.java.spring.project.promotion.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IntegrationException extends RuntimeException {

    private String code;

    public IntegrationException(String code, String message) {
        super(message);
        this.code = code;
    }

}
