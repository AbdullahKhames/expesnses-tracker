package name.expenses.utils;

import name.expenses.error.exception.ErrorCode;
import name.expenses.error.exception.GeneralFailureException;
import name.expenses.globals.PageReq;
import name.expenses.globals.responses.ResponseDto;

import java.util.Map;
import java.util.Set;

public class ValidateInputUtils {
    public static boolean isValidInput(Object category, Object association){
        return category != null && association != null;
    }
    public static void isValidInput(Object ...objects){
        if (objects == null) {
//            throw new GeneralFailureException(ErrorCode.MISSING_PASSWORD.getErrorCode(),
//                    Map.of("error", "the parameter must not be null"));
            return;
        }
        for (Object obj: objects){
            if (obj == null) {
                throw new GeneralFailureException(ErrorCode.MISSING_PASSWORD.getErrorCode(),
                        Map.of("error", "the parameters must not be null"));
            }
        }
    }
    public static <T> ResponseDto validateWildCardSet(Set<?> associationsUpdateDto, Class<T> clazz) {
        try {
            if (associationsUpdateDto.isEmpty()) {
                return ResponseDtoBuilder.getErrorResponse(810, "No associations provided");
            }

            for (Object obj : associationsUpdateDto) {
                if (!clazz.isInstance(obj)) {
                    return ResponseDtoBuilder.getErrorResponse(810, "Associations must be of type " + clazz.getSimpleName());
                }
            }
            return null;
        } catch (Exception exception) {
            return ResponseDtoBuilder.getErrorResponse(815, "error casting the iterator of wildcard to type " + clazz.getSimpleName());
        }
    }
    public static <T> ResponseDto validateEntity(Object obj, Class<T> clazz) {
        try {
            if (obj == null) {
                return ResponseDtoBuilder.getErrorResponse(810, "No obj provided");
            }
            if (!clazz.isInstance(obj)) {
                return ResponseDtoBuilder.getErrorResponse(810, "obj must be of type " + clazz.getSimpleName());
            }
            return null;
        } catch (Exception exception) {
            return ResponseDtoBuilder.getErrorResponse(815, "error casting the obj to type " + clazz.getSimpleName());
        }
    }

    public static PageReq validatePageData(Long pageNumber, Long pageSize){
        if (pageNumber < 1){
            pageNumber = 1L;
        }
        if (pageSize < 1)
        {
            pageSize = 10L;
        }
        return new PageReq(pageNumber, pageSize);
    }
}