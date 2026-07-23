package ru.otus.java.spring.project.promotion.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestException extends RuntimeException {

    private String parameter;

    public RequestException(String parameter, String message) {
        super(message);
        this.parameter = parameter;
    }

}
