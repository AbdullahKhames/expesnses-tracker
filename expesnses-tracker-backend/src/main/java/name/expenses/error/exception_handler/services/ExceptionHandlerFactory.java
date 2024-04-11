package name.expenses.error.exception_handler.services;


import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import name.expenses.error.exception.ApiValidationException;
import name.expenses.error.exception.CustomException;
import name.expenses.error.exception_handler.handlers.ApiValidationExceptionHandler;
import name.expenses.error.exception_handler.handlers.ConstraintViolationExceptionHandler;
import name.expenses.error.exception_handler.handlers.CustomExceptionHandler;
import name.expenses.error.exception_handler.handlers.GeneralExceptionHandler;

import javax.naming.InitialContext;

@Singleton
@Slf4j
public class ExceptionHandlerFactory {
    @Inject
    @Named("apiValidationExceptionHandler")
    private ApiValidationExceptionHandler apiValidationExceptionHandler;

    @Inject
    @Named("customExceptionHandler")
    private CustomExceptionHandler customExceptionHandler;

    @Inject
    @Named("generalExceptionHandler")
    private GeneralExceptionHandler generalExceptionHandler;
    @Inject
    @Named("constraintViolationExceptionHandler")
    private ConstraintViolationExceptionHandler constraintViolationExceptionHandler;

    public ExceptionHandlerStrategy getExceptionHandler(Throwable throwable) {
        try{
            InitialContext context = new InitialContext();
            if (throwable instanceof ApiValidationException) {
                log.info("got from context{}", apiValidationExceptionHandler);
                return apiValidationExceptionHandler;
            } else if (throwable instanceof CustomException) {
                log.info("got from context{}", customExceptionHandler);
                return customExceptionHandler;
            }else if (throwable instanceof ConstraintViolationException) {
                log.info("got from context{}", constraintViolationExceptionHandler);
                return constraintViolationExceptionHandler;
            } else {
                return generalExceptionHandler;
            }
        }catch (Exception ex){
            return new GeneralExceptionHandler();
        }
    }
}
