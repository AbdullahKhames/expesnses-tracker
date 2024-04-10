package name.expenses.features.expesnse.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import name.expenses.features.expesnse.dtos.request.ExpenseReqDto;
import name.expenses.features.expesnse.dtos.request.ExpenseUpdateDto;
import name.expenses.features.expesnse.models.Expense;
import name.expenses.features.expesnse.service.ExpenseService;
import name.expenses.features.expesnse.service.ExpenseServiceStateFull;
import name.expenses.globals.SortDirection;
import name.expenses.globals.responses.ResponseDto;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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
    public Response createExpense(ExpenseReqDto expense){
        try {
            ResponseDto responseDto = expenseService.createExpense(expense);
            if (responseDto != null) {
                return Response.ok(responseDto).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } catch (JsonProcessingException e) {
            return Response.noContent().build();
        }
    }
    @GET
    @Path("/{refNo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getExpense(@PathParam("refNo") String refNo) {
        ResponseDto responseDto = expenseService.getExpense(refNo);
        if (responseDto != null) {
            return Response.ok(responseDto).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @PUT
    @Path("/{refNo}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateExpense(@PathParam("refNo") String refNo,@Valid ExpenseUpdateDto expenseUpdateDto) throws JsonProcessingException {
        ResponseDto responseDto = expenseService.updateExpense(refNo, expenseUpdateDto);
        return Response.ok(responseDto).build();
    }

    @DELETE
    @Path("/{refNo}")
    public Response deleteExpense(@PathParam("refNo") String refNo) throws JsonProcessingException {
        ResponseDto responseDto = expenseService.deleteExpense(refNo);
        return Response.ok(responseDto).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllEntities(
            @QueryParam("page") Integer pageNumber,
            @QueryParam("size") Integer pageSize,
            @QueryParam("sortBy") String sortBy,
            @QueryParam("sortDirection") String direction) {
        // Set default values if parameters are not present
        if (pageNumber == null) {
            pageNumber = 1; // Default page number
        }
        if (pageSize == null) {
            pageSize = 10; // Default page size
        }
        if (sortBy == null) {
            sortBy = "id"; // Default sort by field
        }
        SortDirection sortDirection;
        if (direction == null || direction.isBlank() || direction.equalsIgnoreCase("ASC")) {
            sortDirection = SortDirection.ASC;
        }else if (direction.equalsIgnoreCase("DESC")){
            sortDirection = SortDirection.DESC;
        }else {
            sortDirection = SortDirection.ASC;
        }
        ResponseDto responseDto = expenseService.getAllEntities(pageNumber, pageSize, sortBy, sortDirection);
        return Response.ok(responseDto).build();

    }
}
