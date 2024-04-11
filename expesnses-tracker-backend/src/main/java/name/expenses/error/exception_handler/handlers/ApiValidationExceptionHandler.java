package name.expenses.error.exception_handler.handlers;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import name.expenses.error.exception.ApiValidationException;
import name.expenses.error.exception_handler.models.ErrorCategory;
import name.expenses.error.exception_handler.services.ExceptionHandlerStrategy;
import name.expenses.error.exception_handler.models.ResponseError;
import name.expenses.globals.responses.ResponseDto;
import name.expenses.utils.ResponseDtoBuilder;

import java.util.List;
@Singleton
@Named("apiValidationExceptionHandler")
public class ApiValidationExceptionHandler implements ExceptionHandlerStrategy {
    @Override
    public ResponseDto handleException(Throwable throwable) {
        ApiValidationException e = (ApiValidationException) throwable;
        e.printStackTrace();
        List<String> statusMsgList = e.getStatusMsgList();

        ResponseDto responseDto = ResponseDtoBuilder.getErrorResponse(810, null);
        try {
            ResponseError responseError = new ResponseError();

            responseError.setErrorCode(ApiValidationException.validation_Error_Code);
            responseError.setErrorMsgList(statusMsgList);
            responseError.setErrorCategory(ErrorCategory.BusinessError);
            responseDto.setData(responseError);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return responseDto;
    }
}