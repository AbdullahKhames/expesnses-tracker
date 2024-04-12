package name.expenses.utils;

import name.expenses.error.exception.ErrorCode;
import name.expenses.error.exception.GeneralFailureException;

import java.util.Map;

public class ValidateInputUtils {
    public static boolean isValidInput(Object category, Object association){
        return category != null && association != null;
    }
    public static void isValidInput(Object ...objects){
        if (objects == null) {
            throw new GeneralFailureException(ErrorCode.MISSING_PASSWORD.getErrorCode(),
                    Map.of("error", "the parameter must not be null"));
        }
        for (Object obj: objects){
            if (obj == null) {
                throw new GeneralFailureException(ErrorCode.MISSING_PASSWORD.getErrorCode(),
                        Map.of("error", "the parameters must not be null"));
            }
        }
    }
}