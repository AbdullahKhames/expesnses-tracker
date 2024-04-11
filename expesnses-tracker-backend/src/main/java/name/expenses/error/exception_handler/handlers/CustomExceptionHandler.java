package name.expenses.error.exception_handler.handlers;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import name.expenses.error.exception.CustomException;
import name.expenses.error.exception_handler.services.CachableApiError;
import name.expenses.error.exception_handler.models.ErrorCategory;
import name.expenses.error.exception_handler.services.ExceptionHandlerStrategy;
import name.expenses.error.exception_handler.models.APIError;
import name.expenses.error.exception_handler.models.ResponseError;
import name.expenses.globals.responses.ResponseDto;
import name.expenses.utils.ResponseDtoBuilder;

@Singleton
@Named("customExceptionHandler")
public class CustomExceptionHandler implements ExceptionHandlerStrategy {
    @Inject
    private CachableApiError cachableApiError;

    @Override
    public ResponseDto handleException(Throwable throwable) {
        CustomException e = (CustomException) throwable;
        e.printStackTrace();
        String errorCode = e.getErrorCode();
        ResponseDto responseDto = ResponseDtoBuilder.getErrorResponse(810, null);
        try {
            APIError apiError = cachableApiError.getExpensesAPIError(errorCode);
            ResponseError responseError = new ResponseError();


            responseError.setErrorCode(errorCode);
            responseError.setErrorMessage(apiError.getErrorDescription());
            responseError.setErrorCategory(ErrorCategory.BusinessError);
            responseDto.setData(responseError);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return responseDto;
    }
}