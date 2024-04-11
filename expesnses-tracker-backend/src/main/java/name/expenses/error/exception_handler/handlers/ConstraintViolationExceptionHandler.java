package name.expenses.error.exception_handler.handlers;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolationException;
import name.expenses.error.exception_handler.services.ExceptionHandlerStrategy;
import name.expenses.globals.responses.ResponseDto;
import name.expenses.utils.ResponseDtoBuilder;

import java.util.HashMap;
import java.util.Map;

@Singleton
@Named("constraintViolationExceptionHandler")
public class ConstraintViolationExceptionHandler implements ExceptionHandlerStrategy {
    @Override
    public ResponseDto handleException(Throwable throwable) {
        ConstraintViolationException e = (ConstraintViolationException) throwable;
        Map<String, String> res = new HashMap<>();
        e.getConstraintViolations().forEach(violation -> {
            String fieldName = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            res.put(fieldName, message);
        });
        return ResponseDtoBuilder.getErrorResponse(811, res);
    }
}
