package name.expenses.error.exception_handler.services;

import name.expenses.globals.responses.ResponseDto;

public interface ExceptionHandlerStrategy {
    public ResponseDto handleException(Throwable throwable);

}
