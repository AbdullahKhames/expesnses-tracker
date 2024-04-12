package name.expenses.config.filters;


import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import name.expenses.error.exception_handler.ResponseExceptionBuilder;
import name.expenses.features.category.dtos.request.CategoryReqDto;
import name.expenses.features.category.dtos.request.CategoryUpdateDto;
import name.expenses.features.expesnse.dtos.request.ExpenseReqDto;
import name.expenses.features.expesnse.dtos.request.ExpenseUpdateDto;
import name.expenses.features.sub_category.dtos.request.SubCategoryReqDto;
import name.expenses.features.sub_category.dtos.request.SubCategoryUpdateDto;
import name.expenses.utils.ValidateInputUtils;
import name.expenses.utils.validators.ValidatorUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class RepoAdvice implements Serializable {
    private final ResponseExceptionBuilder responseExceptionBuilder;
    @AroundInvoke
    public Object handleException(InvocationContext context) throws Exception {
        try {
            log.info("calling method {}", context.getMethod().getName());
            log.info("with parameters :");
            Object []parameters = context.getParameters();
            ValidateInputUtils.isValidInput(parameters);

            return context.proceed();
        } catch (Exception e) {
            log.error("Global exception handler caught an exception: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(responseExceptionBuilder.buildResponse(e))
                    .build();
        }
    }
}
