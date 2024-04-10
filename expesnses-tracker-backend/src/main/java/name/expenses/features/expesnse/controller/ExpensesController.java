package name.expenses.features.expesnse.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import name.expenses.features.expesnse.models.Expense;
import name.expenses.features.expesnse.service.ExpenseService;
import name.expenses.features.expesnse.service.ExpenseServiceStateFull;

import java.io.IOException;
import java.io.InputStream;

@Path("/expenses")
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ExpensesController {
    private final ExpenseServiceStateFull expenseServiceStateFull;
    private final ExpenseService expenseService;
    @POST
    @Path("stateful")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createExpense(InputStream is, @HeaderParam("Content-Type") String contentType) throws IOException {
        if (contentType != null && contentType.equals(MediaType.APPLICATION_XML)) {
            return expenseServiceStateFull.createExpenseFromXML(is);
        } else {
            return expenseServiceStateFull.createExpenseFromJSON(is);
        }
    }
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createExpense(Expense expense) throws JsonProcessingException {
        return expenseService.createExpense(expense);
    }
}
