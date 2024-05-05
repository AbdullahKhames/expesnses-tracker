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
import name.expenses.utils.PageUtil;

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
    @Path("/refNo/{refNo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getExpense(@PathParam("refNo") String refNo) {
        ResponseDto responseDto = expenseService.get(refNo);
        if (responseDto != null) {
            return Response.ok(responseDto).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
    @GET
    @Path("/name/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getExpenseByName(@PathParam("name") String name) {
        ResponseDto responseDto = expenseService.getExpenseByName(name);
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
    public Response updateExpense(@PathParam("refNo") String refNo, ExpenseUpdateDto expenseUpdateDto) {
        ResponseDto responseDto = expenseService.update(refNo, expenseUpdateDto);
        return Response.ok(responseDto).build();
    }

    @DELETE
    @Path("/{refNo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteExpense(@PathParam("refNo") String refNo) {
        ResponseDto responseDto = expenseService.delete(refNo);
        return Response.ok(responseDto).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllEntities(
            @DefaultValue("1") @QueryParam("page") Long pageNumber,
            @DefaultValue("10") @QueryParam("per_page") Long pageSize,
            @DefaultValue("id") @QueryParam("sortBy") String sortBy,
            @QueryParam("sortDirection") String direction) {
        SortDirection sortDirection = PageUtil.getSortDirection(direction);
        ResponseDto responseDto = expenseService.getAllEntities(pageNumber, pageSize, sortBy, sortDirection);
        return Response.ok(responseDto).build();

    }
    @GET
    @Path("/noSubCategory")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllEntitiesWithoutSubCategory(
            @DefaultValue("1") @QueryParam("page") Long pageNumber,
            @DefaultValue("10") @QueryParam("per_page") Long pageSize,
            @DefaultValue("id") @QueryParam("sortBy") String sortBy,
            @QueryParam("sortDirection") String direction) {
        SortDirection sortDirection = PageUtil.getSortDirection(direction);
        ResponseDto responseDto = expenseService.getAllEntitiesWithoutSubCategory(pageNumber, pageSize, sortBy, sortDirection);
        return Response.ok(responseDto).build();

    }

}
