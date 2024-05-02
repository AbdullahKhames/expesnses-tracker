package name.expenses.error.exception_handler.handlers;

import com.google.protobuf.MapEntry;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import name.expenses.error.exception.ErrorCode;
import name.expenses.error.exception.GeneralFailureException;
import name.expenses.error.exception_handler.models.ErrorCategory;
import name.expenses.error.exception_handler.services.ExceptionHandlerStrategy;
import name.expenses.error.exception_handler.models.ResponseError;
import name.expenses.globals.responses.ResponseDto;
import name.expenses.utils.ResponseDtoBuilder;

import java.util.Map;

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

        if (e instanceof GeneralFailureException ){
            ex= (GeneralFailureException) e;
        }else if( (e.getCause() != null && e.getCause() instanceof GeneralFailureException)){
            ex= (GeneralFailureException) e.getCause();
        }else if ( (e.getCause() != null && e.getCause().getCause() != null && e.getCause().getCause() instanceof GeneralFailureException)){
            ex= (GeneralFailureException) e.getCause().getCause();
        }else if ( (e.getCause().getCause() != null && e.getCause().getCause().getCause() != null && e.getCause().getCause().getCause() instanceof GeneralFailureException)){
            ex= (GeneralFailureException) e.getCause().getCause();
        }

        if(ex!= null){
            if (ex.getVarMap() != null){
                StringBuilder sb = new StringBuilder();
                for (Map.Entry<String, String> entry: ex.getVarMap().entrySet()){
                    sb.append(String.format("%s : %s ; ", entry.getKey(), entry.getValue()));
                }
                responseError.setErrorMessage(sb.toString());
                responseError.setErrorCategory(ErrorCategory.BusinessError);
                responseError.setErrorCode(ex.getErrorCode());
            }else {
                responseError.setErrorCategory(ErrorCategory.BusinessError);
                responseError.setErrorCode("API_000");
                responseError.setErrorMessage(ex.getMessage());
            }
        }else {
            if (e.getMessage() != null) {
                responseError.setErrorMessage(e.getMessage());
            }else {
                responseError.setErrorMessage(e.getLocalizedMessage());
            }
            responseError.setErrorCode(GeneralFailureException.GENERAL_ERROR);
            responseError.setErrorCategory(ErrorCategory.BusinessError);
        }
        responseDto.setData(responseError);
        return responseDto;
    }
}