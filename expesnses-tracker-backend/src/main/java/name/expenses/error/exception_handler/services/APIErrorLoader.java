package name.expenses.error.exception_handler.services;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import name.expenses.error.exception_handler.models.APIError;

@Slf4j
@Stateless
public class APIErrorLoader {
    @PersistenceContext(name = "expenses-unit")
    EntityManager entityManager;

    public APIError loadApiError(String errorCode) {
        APIError apiError = null;

        try {
            apiError = entityManager.find(APIError.class, errorCode);
        } catch (Exception e) {
            log.error("##########  Exception APIErrorLoader loadApiError for errorCode: '" + errorCode + "'");
            e.printStackTrace();
        }

        return apiError;
    }
}