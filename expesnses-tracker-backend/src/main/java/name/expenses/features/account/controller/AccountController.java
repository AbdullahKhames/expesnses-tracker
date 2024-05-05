package name.expenses.features.account.controller;

import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import name.expenses.config.advice.AroundAdvice;

import name.expenses.features.account.dtos.request.AccountReqDto;
import name.expenses.features.account.dtos.request.AccountUpdateDto;
import name.expenses.features.account.service.AccountService;
import name.expenses.globals.SortDirection;
import name.expenses.globals.responses.ResponseDto;
import name.expenses.utils.PageUtil;

@Path("/accounts")
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Inject})
@Interceptors(AroundAdvice.class)
public class AccountController {
    private final AccountService accountService;
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAccount(AccountReqDto account){
        ResponseDto responseDto = accountService.create(account);
        if (responseDto != null) {
            return Response.ok(responseDto).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
    @GET
    @Path("/refNo/{refNo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccount(@PathParam("refNo") String refNo) {
        ResponseDto responseDto = accountService.get(refNo);
        if (responseDto != null) {
            return Response.ok(responseDto).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
    @GET
    @Path("/name/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccountByName(@PathParam("name") String name) {
        ResponseDto responseDto = accountService.getAccountByName(name);
        if (responseDto != null) {
            return Response.ok(responseDto).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("/{refNo}/pockets")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccountPockets(@PathParam("refNo") String refNo) {
        ResponseDto responseDto = accountService.getAccountPOckets(refNo);
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
    public Response updateAccount(@PathParam("refNo") String refNo, AccountUpdateDto accountUpdateDto) {
        ResponseDto responseDto = accountService.update(refNo, accountUpdateDto);
        return Response.ok(responseDto).build();
    }

    @DELETE
    @Path("/{refNo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAccount(@PathParam("refNo") String refNo) {
        ResponseDto responseDto = accountService.delete(refNo);
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
        ResponseDto responseDto = accountService.getAllEntities(pageNumber, pageSize, sortBy, sortDirection);
        return Response.ok(responseDto).build();

    }

    @PUT
    @Path("/addAssociation/{accountRefNo}/{pocketRefNo}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAssociation(@PathParam("accountRefNo") String accountRefNo, @PathParam("pocketRefNo") String pocketRefNo) {
        ResponseDto responseDto = accountService.addAssociation(accountRefNo, pocketRefNo);
        return Response.ok(responseDto).build();
    }
    @PUT
    @Path("/removeAssociation/{accountRefNo}/{pocketRefNo}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeAssociation(@PathParam("accountRefNo") String accountRefNo, @PathParam("pocketRefNo") String pocketRefNo) {
        ResponseDto responseDto = accountService.removeAssociation(accountRefNo, pocketRefNo);
        return Response.ok(responseDto).build();
    }

}
