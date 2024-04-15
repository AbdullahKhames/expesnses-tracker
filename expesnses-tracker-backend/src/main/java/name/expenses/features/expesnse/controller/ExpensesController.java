package name.expenses.features.expesnse.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import name.expenses.config.advice.AroundAdvice;
import name.expenses.features.expesnse.dtos.request.ExpenseReqDto;
import name.expenses.features.expesnse.dtos.request.ExpenseUpdateDto;
import name.expenses.features.expesnse.service.ExpenseService;
import name.expenses.features.expesnse.service.ExpenseServiceStateFull;
import name.expenses.globals.SortDirection;
import name.expenses.globals.responses.ResponseDto;

import java.io.IOException;
import java.io.InputStream;

@Path("/expenses")
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Inject})
@Interceptors(AroundAdvice.class)
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
    @Produces(MediaType.APPLICATION_JSON)
    public Response createExpense(ExpenseReqDto expense){
        ResponseDto responseDto = expenseService.create(expense);
        if (responseDto != null) {
            return Response.ok(responseDto).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
    @GET
    @Path("/{refNo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getExpense(@PathParam("refNo") String refNo) {
        ResponseDto responseDto = expenseService.get(refNo);
        if (responseDto != null) {
            return Response.ok(responseDto).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @PUT
    @Path("/{refNo}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateExpense(@PathParam("refNo") String refNo, ExpenseUpdateDto expenseUpdateDto) throws JsonProcessingException {
        ResponseDto responseDto = expenseService.update(refNo, expenseUpdateDto);
        return Response.ok(responseDto).build();
    }

    @DELETE
    @Path("/{refNo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteExpense(@PathParam("refNo") String refNo) throws JsonProcessingException {
        ResponseDto responseDto = expenseService.delete(refNo);
        return Response.ok(responseDto).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllEntities(
            @QueryParam("page") Long pageNumber,
            @QueryParam("size") Long pageSize,
            @QueryParam("sortBy") String sortBy,
            @QueryParam("sortDirection") String direction) {
        if (pageNumber == null) {
            pageNumber = 1L;
        }
        if (pageSize == null) {
            pageSize = 10L;
        }
        if (sortBy == null) {
            sortBy = "id";
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
