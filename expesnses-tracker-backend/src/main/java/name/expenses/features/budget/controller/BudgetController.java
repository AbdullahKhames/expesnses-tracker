package name.expenses.features.budget.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import name.expenses.config.advice.AroundAdvice;

import name.expenses.error.exception_handler.ResponseExceptionBuilder;

import name.expenses.features.budget.dtos.request.BudgetReqDto;
import name.expenses.features.budget.dtos.request.BudgetUpdateDto;
import name.expenses.features.budget.service.BudgetService;
import name.expenses.globals.SortDirection;
import name.expenses.globals.responses.ResponseDto;
import name.expenses.utils.PageUtil;

@Path("/budgets")
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Inject})
@Interceptors(AroundAdvice.class)
public class BudgetController {
    private final BudgetService budgetService;
    private final ResponseExceptionBuilder responseExceptionBuilder;

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces(MediaType.APPLICATION_JSON)
    public Response createBudget(String rawRequestBody){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            BudgetReqDto request = objectMapper.readValue(rawRequestBody, BudgetReqDto.class);
            return Response.ok(budgetService.create(request)).build();
        } catch (Exception e) {
            // Handle the deserialization error here...
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(responseExceptionBuilder.buildResponse(e))
                    .build();
        }
    }
    @GET
    @Path("/refNo/{refNo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBudget(@PathParam("refNo") String refNo) {
        ResponseDto responseDto = budgetService.get(refNo);
        if (responseDto != null) {
            return Response.ok(responseDto).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
    @GET
    @Path("/name/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBudgetByName(@PathParam("name") String name) {
        ResponseDto responseDto = budgetService.getBudgetByName(name);
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
    public Response updateBudget(@PathParam("refNo") String refNo, String rawRequestBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            BudgetUpdateDto request = objectMapper.readValue(rawRequestBody, BudgetUpdateDto.class);
            return Response.ok(budgetService.update(refNo, request)).build();
        } catch (Exception e) {
            // Handle the deserialization error here...
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(responseExceptionBuilder.buildResponse(e))
                    .build();
        }
    }

    @DELETE
    @Path("/{refNo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteBudget(@PathParam("refNo") String refNo) {
        ResponseDto responseDto = budgetService.delete(refNo);
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
        ResponseDto responseDto = budgetService.getAllEntities(pageNumber, pageSize, sortBy, sortDirection);
        return Response.ok(responseDto).build();

    }
    @GET
    @Path("/noAccount")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllEntitiesWithoutAccount(
            @DefaultValue("1") @QueryParam("page") Long pageNumber,
            @DefaultValue("10") @QueryParam("per_page") Long pageSize,
            @DefaultValue("id") @QueryParam("sortBy") String sortBy,
            @QueryParam("sortDirection") String direction) {
        SortDirection sortDirection = PageUtil.getSortDirection(direction);
        ResponseDto responseDto = budgetService.getAllEntitiesWithoutAccount(pageNumber, pageSize, sortBy, sortDirection);
        return Response.ok(responseDto).build();

    }
}
