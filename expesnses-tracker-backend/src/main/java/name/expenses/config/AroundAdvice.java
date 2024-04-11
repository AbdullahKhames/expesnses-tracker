package name.expenses.config;


import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
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
import name.expenses.utils.validators.ValidatorUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Stateless
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class AroundAdvice {

    private final ResponseExceptionBuilder responseExceptionBuilder;
    private final ValidatorUtils validatorUtils;
    private final static List<Class<?>> classesToValidate = new ArrayList<>();

    static {
        classesToValidate.add(ExpenseReqDto.class);
        classesToValidate.add(ExpenseUpdateDto.class);
        classesToValidate.add(SubCategoryReqDto.class);
        classesToValidate.add(SubCategoryUpdateDto.class);
        classesToValidate.add(CategoryReqDto.class);
        classesToValidate.add(CategoryUpdateDto.class);

    }
    @AroundInvoke
    public Object handleException(InvocationContext context) throws Exception {
        try {
            log.info("calling method {}", context.getMethod().getName());
            log.info("with parameters :");
            Object []parameters = context.getParameters();
            for (int i = 0; i < parameters.length; i++){
                log.info("parameter index #{}", i);
                log.info("parameter value #{}", parameters[i]);
                if (parameters[i] != null && classesToValidate.contains(parameters[i].getClass())){
                    Set<ConstraintViolation<Object>> constraintViolations = validatorUtils.hasValidationErrors(parameters[i]);
                    if (constraintViolations != null && !constraintViolations.isEmpty()){
                        throw new ConstraintViolationException(constraintViolations);
                    }
                }
            }

            return context.proceed();
        } catch (Exception e) {
            log.error("Global exception handler caught an exception: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(responseExceptionBuilder.buildResponse(e))
                    .build();
        }
    }
}
