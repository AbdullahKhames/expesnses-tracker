package name.expenses.features.budget_transfer.controller;

import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import name.expenses.config.advice.AroundAdvice;


import name.expenses.features.budget_transfer.dtos.request.BudgetTransferReqDto;
import name.expenses.features.budget_transfer.dtos.request.BudgetTransferUpdateDto;
import name.expenses.features.budget_transfer.service.BudgetTransferService;
import name.expenses.globals.SortDirection;
import name.expenses.globals.responses.ResponseDto;
import name.expenses.utils.PageUtil;

@Path("/budget-transfers")
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Inject})
@Interceptors(AroundAdvice.class)
public class BudgetTransferController {
    private final BudgetTransferService transactionService;
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces(MediaType.APPLICATION_JSON)
    public Response createBudgetTransfer(@Valid BudgetTransferReqDto transaction){
        ResponseDto responseDto = transactionService.create(transaction);
        if (responseDto != null) {
            return Response.ok(responseDto).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
    @GET
    @Path("/{refNo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBudgetTransfer(@PathParam("refNo") String refNo) {
        ResponseDto responseDto = transactionService.get(refNo);
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
    public Response updateBudgetTransfer(@PathParam("refNo") String refNo, BudgetTransferUpdateDto transactionUpdateDto) {
        ResponseDto responseDto = transactionService.update(refNo, transactionUpdateDto);
        return Response.ok(responseDto).build();
    }

    @DELETE
    @Path("/{refNo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteBudgetTransfer(@PathParam("refNo") String refNo) {
        ResponseDto responseDto = transactionService.delete(refNo);
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
        ResponseDto responseDto = transactionService.getAllEntities(pageNumber, pageSize, sortBy, sortDirection);
        return Response.ok(responseDto).build();

    }

}
