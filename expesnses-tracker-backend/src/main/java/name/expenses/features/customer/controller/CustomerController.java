package name.expenses.features.customer.controller;

import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import name.expenses.config.advice.AroundAdvice;
import name.expenses.features.association.AssociationManager;
import name.expenses.features.association.Models;
import name.expenses.features.association.dto.AssociationGenericReqDto;
import name.expenses.features.association.dto.AssociationReqDto;
import name.expenses.features.customer.dtos.request.CustomerReqDto;
import name.expenses.features.customer.dtos.request.CustomerUpdateDto;
import name.expenses.features.customer.service.CustomerService;
import name.expenses.features.expesnse.dtos.request.ExpenseReqDto;
import name.expenses.features.budget.dtos.request.BudgetReqDto;
import name.expenses.globals.SortDirection;
import name.expenses.globals.responses.ResponseDto;
import name.expenses.utils.PageUtil;

@Path("/customers")
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Inject})
@Interceptors(AroundAdvice.class)
public class CustomerController {
    private final CustomerService customerService;
    private final AssociationManager associationManager;


    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCustomer(CustomerReqDto customer){
        ResponseDto responseDto = customerService.create(customer);
        if (responseDto != null) {
            return Response.ok(responseDto).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
    @GET
    @Path("/{refNo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomer(@PathParam("refNo") String refNo) {
        ResponseDto responseDto = customerService.get(refNo);
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
    public Response updateCustomer(@PathParam("refNo") String refNo, CustomerUpdateDto customerUpdateDto) {
        ResponseDto responseDto = customerService.update(refNo, customerUpdateDto);
        return Response.ok(responseDto).build();
    }

    @DELETE
    @Path("/{refNo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCustomer(@PathParam("refNo") String refNo) {
        ResponseDto responseDto = customerService.delete(refNo);
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
        ResponseDto responseDto = customerService.getAllEntities(pageNumber, pageSize, sortBy, sortDirection);
        return Response.ok(responseDto).build();

    }
    @GET
    @Path("/accounts")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCustomerAccounts(
            @DefaultValue("1") @QueryParam("page") Long pageNumber,
            @DefaultValue("10") @QueryParam("per_page") Long pageSize,
            @DefaultValue("id") @QueryParam("sortBy") String sortBy,
            @QueryParam("sortDirection") String direction) {
        SortDirection sortDirection = PageUtil.getSortDirection(direction);
        ResponseDto responseDto = customerService.getAllCustomerAccounts(pageNumber, pageSize, sortBy, sortDirection);
        return Response.ok(responseDto).build();

    }
    @GET
    @Path("/accounts/{accountRef}/budgets")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCustomerAccountbudgets(
            @PathParam("accountRef") String accountRef,
            @DefaultValue("1") @QueryParam("page") Long pageNumber,
            @DefaultValue("10") @QueryParam("per_page") Long pageSize,
            @DefaultValue("id") @QueryParam("sortBy") String sortBy,
            @QueryParam("sortDirection") String direction) {
        SortDirection sortDirection = PageUtil.getSortDirection(direction);
        ResponseDto responseDto = customerService.getAllCustomerAccountBudgets(accountRef, pageNumber, pageSize, sortBy, sortDirection);
        return Response.ok(responseDto).build();

    }

    @GET
    @Path("/categories")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCustomerCategories(
            @DefaultValue("1") @QueryParam("page") Long pageNumber,
            @DefaultValue("10") @QueryParam("per_page") Long pageSize,
            @DefaultValue("id") @QueryParam("sortBy") String sortBy,
            @QueryParam("sortDirection") String direction) {
        SortDirection sortDirection = PageUtil.getSortDirection(direction);
        ResponseDto responseDto = customerService.getAllCustomerCategories(pageNumber, pageSize, sortBy, sortDirection);
        return Response.ok(responseDto).build();

    }
    @GET
    @Path("/categories/{categoryRef}/sub-categories")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCustomerCategorySubCategories(
            @PathParam("categoryRef") String categoryRef,
            @DefaultValue("1") @QueryParam("page") Long pageNumber,
            @DefaultValue("10") @QueryParam("per_page") Long pageSize,
            @DefaultValue("id") @QueryParam("sortBy") String sortBy,
            @QueryParam("sortDirection") String direction) {
        SortDirection sortDirection = PageUtil.getSortDirection(direction);
        ResponseDto responseDto = customerService.getAllCustomerCategorySubCategories(categoryRef, pageNumber, pageSize, sortBy, sortDirection);
        return Response.ok(responseDto).build();
    }

    @GET
    @Path("/sub-categories")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCustomerSubCategories(
            @DefaultValue("1") @QueryParam("page") Long pageNumber,
            @DefaultValue("10") @QueryParam("per_page") Long pageSize,
            @DefaultValue("id") @QueryParam("sortBy") String sortBy,
            @QueryParam("sortDirection") String direction) {
        SortDirection sortDirection = PageUtil.getSortDirection(direction);
        ResponseDto responseDto = customerService.getAllCustomerSubCategories(pageNumber, pageSize, sortBy, sortDirection);
        return Response.ok(responseDto).build();

    }
    @GET
    @Path("/sub-categories/{subCategoryRef}/expenses")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCustomerSubCategoryExpenses(
            @PathParam("subCategoryRef") String subCategoryRef,
            @DefaultValue("1") @QueryParam("page") Long pageNumber,
            @DefaultValue("10") @QueryParam("per_page") Long pageSize,
            @DefaultValue("id") @QueryParam("sortBy") String sortBy,
            @QueryParam("sortDirection") String direction) {
        SortDirection sortDirection = PageUtil.getSortDirection(direction);
        ResponseDto responseDto = customerService.getAllCustomerSubCategoryExpenses(subCategoryRef, pageNumber, pageSize, sortBy, sortDirection);
        return Response.ok(responseDto).build();
    }

    @GET
    @Path("/budgets")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCustomerBudgets(
            @DefaultValue("1") @QueryParam("page") Long pageNumber,
            @DefaultValue("10") @QueryParam("per_page") Long pageSize,
            @DefaultValue("id") @QueryParam("sortBy") String sortBy,
            @QueryParam("sortDirection") String direction) {
        SortDirection sortDirection = PageUtil.getSortDirection(direction);
        ResponseDto responseDto = customerService.getAllCustomerBudgets(pageNumber, pageSize, sortBy, sortDirection);
        return Response.ok(responseDto).build();

    }
    @GET
    @Path("/expenses")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCustomerExpenses(
            @DefaultValue("1") @QueryParam("page") Long pageNumber,
            @DefaultValue("10") @QueryParam("per_page") Long pageSize,
            @DefaultValue("id") @QueryParam("sortBy") String sortBy,
            @QueryParam("sortDirection") String direction) {
        SortDirection sortDirection = PageUtil.getSortDirection(direction);
        ResponseDto responseDto = customerService.getAllCustomerExpenses(pageNumber, pageSize, sortBy, sortDirection);
        return Response.ok(responseDto).build();

    }
    @GET
    @Path("/transactions")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCustomerTransactions(
            @DefaultValue("1") @QueryParam("page") Long pageNumber,
            @DefaultValue("10") @QueryParam("per_page") Long pageSize,
            @DefaultValue("id") @QueryParam("sortBy") String sortBy,
            @QueryParam("sortDirection") String direction) {
        SortDirection sortDirection = PageUtil.getSortDirection(direction);
        ResponseDto responseDto = customerService.getAllCustomerTransactions(pageNumber, pageSize, sortBy, sortDirection);
        return Response.ok(responseDto).build();

    }
    @GET
    @Path("/budget-transfers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCustomerBudgetTransfers(
            @DefaultValue("1") @QueryParam("page") Long pageNumber,
            @DefaultValue("10") @QueryParam("per_page") Long pageSize,
            @DefaultValue("id") @QueryParam("sortBy") String sortBy,
            @QueryParam("sortDirection") String direction) {
        SortDirection sortDirection = PageUtil.getSortDirection(direction);
        ResponseDto responseDto = customerService.getAllCustomerBudgetTransfers(pageNumber, pageSize, sortBy, sortDirection);
        return Response.ok(responseDto).build();

    }

    @Path("/add-accounts")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addCustomerAccounts(AssociationReqDto associationReqDto){
        return Response.ok(associationManager.addAssociation(null, Models.CUSTOMER, associationReqDto.getAssociationRefNos(), Models.ACCOUNT)).build();
    }
    @Path("/remove-accounts")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeCustomerAccounts(AssociationReqDto associationReqDto){
        return Response.ok(associationManager.removeAssociation(null, Models.CUSTOMER, associationReqDto.getAssociationRefNos(), Models.ACCOUNT)).build();
    }
    @Path("/add-categories")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addCustomerCategories(AssociationReqDto associationReqDto){
        return Response.ok(associationManager.addAssociation(null, Models.CUSTOMER, associationReqDto.getAssociationRefNos(), Models.CATEGORY)).build();
    }
    @Path("/remove-categories")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeCustomerCategories(AssociationReqDto associationReqDto){
        return Response.ok(associationManager.removeAssociation(null, Models.CUSTOMER, associationReqDto.getAssociationRefNos(), Models.CATEGORY)).build();
    }
    @Path("/add-sub-categories")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addCustomerSubCategories(AssociationReqDto associationReqDto){
        return Response.ok(associationManager.addAssociation(null, Models.CUSTOMER, associationReqDto.getAssociationRefNos(), Models.SUB_CATEGORY)).build();
    }
    @Path("/remove-sub-categories")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeCustomerSubCategories(AssociationReqDto associationReqDto){
        return Response.ok(associationManager.removeAssociation(null, Models.CUSTOMER, associationReqDto.getAssociationRefNos(), Models.SUB_CATEGORY)).build();
    }
    @Path("/add-expenses")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addCustomerExpenses(AssociationReqDto associationReqDto){
        return Response.ok(associationManager.addAssociation(null, Models.CUSTOMER, associationReqDto.getAssociationRefNos(), Models.EXPENSE)).build();
    }
    @Path("/add-expenses-dtos")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addCustomerExpensesDTOS(AssociationGenericReqDto<ExpenseReqDto> associationGenericReqDto){
        return Response.ok(associationManager.addAssociationDtos(null, Models.CUSTOMER, associationGenericReqDto.getAssociationReqDtos(), Models.EXPENSE)).build();
    }
    @Path("/remove-expenses")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeCustomerExpenses(AssociationReqDto associationReqDto){
        return Response.ok(associationManager.removeAssociation(null, Models.CUSTOMER, associationReqDto.getAssociationRefNos(), Models.EXPENSE)).build();
    }
    @Path("/add-budgets")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addCustomerBudgets(AssociationReqDto associationReqDto){
        return Response.ok(associationManager.addAssociation(null, Models.CUSTOMER, associationReqDto.getAssociationRefNos(), Models.Budget)).build();
    }
    @Path("/add-budgets-dtos")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addCustomerBudgetSDTOS(AssociationGenericReqDto<BudgetReqDto> associationGenericReqDto){
        return Response.ok(associationManager.addAssociationDtos(null, Models.CUSTOMER, associationGenericReqDto.getAssociationReqDtos(), Models.Budget)).build();
    }
    @Path("/remove-budgets")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeCustomerBudgets(AssociationReqDto associationReqDto) {
        return Response.ok(associationManager.removeAssociation(null, Models.CUSTOMER, associationReqDto.getAssociationRefNos(), Models.Budget)).build();
    }

    @GET
    @Path("/get-accounts")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomerAccounts() {
        return Response.ok(customerService.getCustomerAssociation(Models.ACCOUNT)).build();
    }
    @GET
    @Path("/get-categories")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomerCategories() {
        return Response.ok(customerService.getCustomerAssociation(Models.CATEGORY)).build();
    }
    @GET
    @Path("/get-sub-categories")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomerSubCategories() {
        return Response.ok(customerService.getCustomerAssociation(Models.SUB_CATEGORY)).build();
    }
    @GET
    @Path("/get-budgets")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomerBudgets() {
        return Response.ok(customerService.getCustomerAssociation(Models.Budget)).build();
    }
    @GET
    @Path("/get-expenses")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomerExpenses() {
        return Response.ok(customerService.getCustomerAssociation(Models.EXPENSE)).build();
    }
    @GET
    @Path("/get-transactions")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomerTransactions() {
        return Response.ok(customerService.getCustomerAssociation(Models.TRANSACTION)).build();
    }

}
