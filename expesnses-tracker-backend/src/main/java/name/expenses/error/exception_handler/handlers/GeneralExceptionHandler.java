package name.expenses.error.exception_handler.handlers;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import name.expenses.error.exception.GeneralFailureException;
import name.expenses.error.exception_handler.models.ErrorCategory;
import name.expenses.error.exception_handler.services.ExceptionHandlerStrategy;
import name.expenses.error.exception_handler.models.ResponseError;
import name.expenses.globals.responses.ResponseDto;
import name.expenses.utils.ResponseDtoBuilder;

@Singleton
@Named("generalExceptionHandler")
public class GeneralExceptionHandler implements ExceptionHandlerStrategy {
    @Override
    public ResponseDto handleException(Throwable throwable) {
        Exception e = (Exception) throwable;
        GeneralFailureException ex=null;
        e.printStackTrace();
        ResponseDto responseDto = ResponseDtoBuilder.getErrorResponse(810, null);
        ResponseError responseError = new ResponseError();

        try {
             ex= (GeneralFailureException) throwable;
        }catch (Exception exception){}

        if(ex!= null &&ex.getVarMap() != null){
            responseError.setErrorMessage(ex.getVarMap().get(ex.getVarMap().keySet().toArray()[0]));
            if(responseError.getErrorCategory()==null) {
                responseError.setErrorCategory(ErrorCategory.BusinessError);
            }
            responseError.setErrorCode(ex.getErrorCode());
        }else {
            responseError.setErrorMessage(e.getMessage());
            responseError.setErrorCategory(ErrorCategory.BusinessError);
        }
        responseDto.setData(responseError);
        return responseDto;
    }
}