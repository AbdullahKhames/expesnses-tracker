package name.expenses.features.transaction.controller;

import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import name.expenses.config.advice.AroundAdvice;


import name.expenses.features.transaction.dtos.request.TransactionReqDto;
import name.expenses.features.transaction.dtos.request.TransactionUpdateDto;
import name.expenses.features.transaction.service.TransactionService;
import name.expenses.globals.SortDirection;
import name.expenses.globals.responses.ResponseDto;

@Path("/transactions")
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Inject})
@Interceptors(AroundAdvice.class)
public class TransactionController {
    private final TransactionService transactionService;
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTransaction(@Valid TransactionReqDto transaction){
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
    public Response getTransaction(@PathParam("refNo") String refNo) {
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
    public Response updateTransaction(@PathParam("refNo") String refNo, TransactionUpdateDto transactionUpdateDto) {
        ResponseDto responseDto = transactionService.update(refNo, transactionUpdateDto);
        return Response.ok(responseDto).build();
    }

    @DELETE
    @Path("/{refNo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteTransaction(@PathParam("refNo") String refNo) {
        ResponseDto responseDto = transactionService.delete(refNo);
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
        ResponseDto responseDto = transactionService.getAllEntities(pageNumber, pageSize, sortBy, sortDirection);
        return Response.ok(responseDto).build();

    }

}
