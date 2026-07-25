package ru.otus.java.spring.project.promotion.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = RequestParameterValidator.class)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParameterValid {
    String message() default "Parameter not valid";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default {};

    Class<?> source();
}
