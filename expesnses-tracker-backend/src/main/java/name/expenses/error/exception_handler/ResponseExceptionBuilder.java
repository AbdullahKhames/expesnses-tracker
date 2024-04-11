package name.expenses.error.exception_handler;



import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import name.expenses.error.exception_handler.services.CachableApiError;
import name.expenses.error.exception_handler.services.ExceptionHandlerFactory;
import name.expenses.error.exception_handler.services.ExceptionHandlerStrategy;
import name.expenses.globals.responses.ResponseDto;

@Stateless
public class ResponseExceptionBuilder {
    @Inject
    private CachableApiError cachableApiError;

    @Inject
    private ExceptionHandlerFactory exceptionHandlerFactory;

   public ResponseDto buildResponse(Throwable ex){
       ExceptionHandlerStrategy exceptionHandlerStrategy = exceptionHandlerFactory.getExceptionHandler(ex);
        return exceptionHandlerStrategy.handleException(ex);
    }

}
