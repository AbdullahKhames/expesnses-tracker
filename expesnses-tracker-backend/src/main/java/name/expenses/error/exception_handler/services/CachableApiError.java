package name.expenses.error.exception_handler.services;

import name.expenses.error.exception_handler.models.APIError;

public interface CachableApiError {
    public APIError getExpensesAPIError(String errorCode) throws Exception;
}
