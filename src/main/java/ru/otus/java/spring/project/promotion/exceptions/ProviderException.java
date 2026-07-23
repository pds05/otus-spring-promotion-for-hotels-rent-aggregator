package ru.otus.java.spring.project.promotion.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProviderException extends RuntimeException {

    private String code;

    public ProviderException(String code, String message) {
        super(message);
        this.code = code;
    }

}
