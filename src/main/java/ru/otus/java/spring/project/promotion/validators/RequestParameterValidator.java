package ru.otus.java.spring.project.promotion.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import ru.otus.java.spring.project.promotion.enums.PromoCampaignType;

@RequiredArgsConstructor
public class RequestParameterValidator implements ConstraintValidator<RequestParameterValid, String> {

    private Class<?> clazz;

    @Override
    public void initialize(RequestParameterValid constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        clazz = constraintAnnotation.source();
    }

    @Override
    public boolean isValid(String request, ConstraintValidatorContext constraintValidatorContext) {
        if (clazz == PromoCampaignType.class) {
            return PromoCampaignType.getPromoCampaignType(request) != null;
        }
        return false;
    }
}
