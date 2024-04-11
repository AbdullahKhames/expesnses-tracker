package name.expenses.error.exception_handler.services;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import name.expenses.error.exception_handler.models.APIError;

import java.util.HashMap;
import java.util.Map;

@Stateless
public class CacheableAPIErrorImpl implements CachableApiError {

    private static Map<String, APIError> ExpensesErrorMap;

    static {
        ExpensesErrorMap = new HashMap<String, APIError>();
    }

    @Inject
    private APIErrorLoader apiErrorLoader;

    public APIError getExpensesAPIError(String errorCode) throws Exception {
        APIError apiError = null;

        if (ExpensesErrorMap.containsKey(errorCode)) {
            apiError = ExpensesErrorMap.get(errorCode);
        } else {
            apiError = apiErrorLoader.loadApiError(errorCode);

            if (apiError != null) {

                ExpensesErrorMap.put(errorCode, apiError);
            }
        }
        return apiError;
    }
}
