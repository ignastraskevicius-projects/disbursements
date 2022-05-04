package org.ignast.challenge.ecommerce.disbursements.api;

import javax.validation.ConstraintViolationException;
import org.ignast.challenge.ecommerce.disbursements.domain.NoDisbursementsFound;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GeneralErrorsControllerAdvice {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleConstraintValidationException(final ConstraintViolationException e) {}

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleNoDisbursementsFound(final NoDisbursementsFound e) {}
}
