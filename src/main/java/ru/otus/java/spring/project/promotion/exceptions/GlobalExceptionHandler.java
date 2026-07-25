package ru.otus.java.spring.project.promotion.exceptions;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorDto> catchMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        ResponseEntity<ValidationErrorDto> response;
        if (exception.getBindingResult().hasFieldErrors()) {
            response = new ResponseEntity<>(
                    new ValidationErrorDto("VALIDATION_ERROR", "Request not valid",
                            exception.getBindingResult().getFieldErrors().stream().map(ve ->
                                    new ValidationFieldErrorDto(ve.getField(), ve.getDefaultMessage())).toList()
                    ),
                    HttpStatus.BAD_REQUEST);

        } else {
            response = new ResponseEntity<>(
                    new ValidationErrorDto("VALIDATION_ERROR", "Request not valid",
                            exception.getBindingResult().getAllErrors().stream().map(oe ->
                                    new ValidationFieldErrorDto("", oe.getDefaultMessage())).toList()
                    ),
                    HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ValidationErrorDto> catchMethodArgumentException(MethodArgumentTypeMismatchException exception) {
        return new ResponseEntity<>(
                new ValidationErrorDto("REQUEST_PARAMETER_ERROR",
                        "Request not valid",
                        Collections.singletonList(new ValidationFieldErrorDto(exception.getName(), "Bad value"))),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = NoResourceFoundException.class)
    public ResponseEntity<ErrorDto> catchNoStaticResource(NoResourceFoundException exception) {
        return new ResponseEntity<>(new ErrorDto("URI_ERROR", "No static resource " + exception.getResourcePath()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorDto> catchConstraintViolationException(ConstraintViolationException exception) {
        log.warn("Request not valid: {}", exception.getMessage());
        return new ResponseEntity<>(
                new ValidationErrorDto("VALIDATION_ERROR", "Request not valid",
                        exception.getConstraintViolations().stream().map(cv ->
                                new ValidationFieldErrorDto(cv.getPropertyPath().toString().split("\\.")[1], cv.getMessage())).toList()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(value = MethodValidationException.class)
    public ResponseEntity<ValidationErrorDto> catchMethodValidationException(MethodValidationException exception) {
        log.warn("Request not valid: {}", exception.getMessage());
        return new ResponseEntity<>(
                new ValidationErrorDto("VALIDATION_ERROR", "Request not valid",
                        exception.getParameterValidationResults().stream().map(result -> new ValidationFieldErrorDto(
                                        result.getMethodParameter().getParameterName(),
                                        result.getResolvableErrors().stream().findFirst().orElseThrow(() ->
                                                new ApplicationException("Validation parse error")).getDefaultMessage()))
                                .collect(Collectors.toUnmodifiableList())
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public ResponseEntity<ValidationErrorDto> catchServletRequestParameterException(MissingServletRequestParameterException exception) {
        log.warn("Request missing parameter {}, message={}", exception.getParameterName(), exception.getMessage());
        return new ResponseEntity<>(
                new ValidationErrorDto("REQUEST_PARAMETER_ERROR", "Missing parameter",
                        List.of(new ValidationFieldErrorDto(exception.getParameterName(), exception.getMessage()))
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(value = DataAccessException.class)
    public ResponseEntity<ErrorDto> catchDataAccessException(DataAccessException exception) {
        log.error("Request processing error: {}", exception.getMessage(), exception);
        return new ResponseEntity<>(new ErrorDto("PROCESSING_ERROR", "Resource unavailable"),
                HttpStatus.UNPROCESSABLE_ENTITY
        );
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorDto> catchException(Exception exception) {
        log.error("Unexpected error has occurred: {}", exception.getMessage(), exception);
        return new ResponseEntity<>(new ErrorDto("INTERNAL_SERVER_ERROR", "Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = ApplicationException.class)
    public ResponseEntity<ErrorDto> catchApplicationException(ApplicationException exception) {
        log.error("Application error has occurred: {}", exception.getMessage(), exception);
        return new ResponseEntity<>(new ErrorDto("INTERNAL_SERVER_ERROR", "Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<ErrorDto> catchResourceNotFoundException(ResourceNotFoundException e) {
        log.warn("Resource not found: {}", e.getMessage());
        return new ResponseEntity<>(new ErrorDto("RESOURCE_NOT_FOUND", e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = IntegrationException.class)
    public ResponseEntity<ErrorDto> catchIntegrationException(IntegrationException e) {
        log.error("Integration with external services error has occurred: {}", e.getMessage(), e);
        return new ResponseEntity<>(new ErrorDto("INTERNAL_SERVER_ERROR", "Server error"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = BusinessLogicException.class)
    public ResponseEntity<ErrorDto> catchBusinessLogicException(BusinessLogicException e) {
        log.error("Business logic error: {}", e.getMessage());
        return new ResponseEntity<>(new ErrorDto(e.getCode(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
