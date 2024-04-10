package name.expenses.features.expesnse.service.service_impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import name.expenses.config.ObjectMapperConfig;
import name.expenses.features.expesnse.dao.ExpenseDAO;
import name.expenses.features.expesnse.models.Expense;
import name.expenses.features.expesnse.service.ExpenseService;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Stateless
public class ExpenseServiceStatelessmpl implements ExpenseService {
    private final ObjectMapper objectMapper = ObjectMapperConfig.getObjectMapper();

    @Inject
    ExpenseDAO expenseDAO;

    @Override
    public Response createExpense(Expense expense) throws JsonProcessingException {
        Expense savedExpense = expenseDAO.createExpense(expense);
        System.out.println("Created expense " + expense.getId());
        System.out.println(expense);
        return Response
                .ok(
                        objectMapper.writeValueAsString(savedExpense),
                        MediaType.APPLICATION_JSON_TYPE
                )
                .build();
    }
}
