package ru.otus.java.spring.project.promotion.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ValidationFieldErrorDto {

    private String field;

    private String message;
}
