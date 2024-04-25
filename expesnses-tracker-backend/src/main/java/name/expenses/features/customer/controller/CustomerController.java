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
import name.expenses.features.pocket.dtos.request.PocketReqDto;
import name.expenses.globals.SortDirection;
import name.expenses.globals.responses.ResponseDto;

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
            @QueryParam("page") Long pageNumber,
            @QueryParam("per_page") Long pageSize,
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
        ResponseDto responseDto = customerService.getAllEntities(pageNumber, pageSize, sortBy, sortDirection);
        return Response.ok(responseDto).build();

    }
    @GET
    @Path("/accounts")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCustomerAccounts(
            @QueryParam("page") Long pageNumber,
            @QueryParam("per_page") Long pageSize,
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
        ResponseDto responseDto = customerService.getAllCustomerAccounts(pageNumber, pageSize, sortBy, sortDirection);
        return Response.ok(responseDto).build();

    }
    @GET
    @Path("/accounts/{accountRef}/pockets")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCustomerAccountPockets(
            @PathParam("accountRef") String accountRef,
            @QueryParam("page") Long pageNumber,
            @QueryParam("per_page") Long pageSize,
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
        ResponseDto responseDto = customerService.getAllCustomerAccountPockets(accountRef, pageNumber, pageSize, sortBy, sortDirection);
        return Response.ok(responseDto).build();

    }

    @GET
    @Path("/categories")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCustomerCategories(
            @QueryParam("page") Long pageNumber,
            @QueryParam("per_page") Long pageSize,
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
        ResponseDto responseDto = customerService.getAllCustomerCategories(pageNumber, pageSize, sortBy, sortDirection);
        return Response.ok(responseDto).build();

    }
    @GET
    @Path("/categories/{categoryRef}/sub-categories")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCustomerCategorySubCategories(
            @PathParam("categoryRef") String categoryRef,
            @QueryParam("page") Long pageNumber,
            @QueryParam("per_page") Long pageSize,
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
        ResponseDto responseDto = customerService.getAllCustomerCategorySubCategories(categoryRef, pageNumber, pageSize, sortBy, sortDirection);
        return Response.ok(responseDto).build();
    }

    @GET
    @Path("/sub-categories")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCustomerSubCategories(
            @QueryParam("page") Long pageNumber,
            @QueryParam("per_page") Long pageSize,
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
        ResponseDto responseDto = customerService.getAllCustomerSubCategories(pageNumber, pageSize, sortBy, sortDirection);
        return Response.ok(responseDto).build();

    }
    @GET
    @Path("/sub-categories/{subCategoryRef}/expenses")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCustomerSubCategoryExpenses(
            @PathParam("subCategoryRef") String subCategoryRef,
            @QueryParam("page") Long pageNumber,
            @QueryParam("per_page") Long pageSize,
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
        ResponseDto responseDto = customerService.getAllCustomerSubCategoryExpenses(subCategoryRef, pageNumber, pageSize, sortBy, sortDirection);
        return Response.ok(responseDto).build();
    }

    @GET
    @Path("/pockets")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCustomerPockets(
            @QueryParam("page") Long pageNumber,
            @QueryParam("per_page") Long pageSize,
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
        ResponseDto responseDto = customerService.getAllCustomerPockets(pageNumber, pageSize, sortBy, sortDirection);
        return Response.ok(responseDto).build();

    }
    @GET
    @Path("/expenses")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCustomerExpenses(
            @QueryParam("page") Long pageNumber,
            @QueryParam("per_page") Long pageSize,
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
        ResponseDto responseDto = customerService.getAllCustomerExpenses(pageNumber, pageSize, sortBy, sortDirection);
        return Response.ok(responseDto).build();

    }
    @GET
    @Path("/transactions")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCustomerTransactions(
            @QueryParam("page") Long pageNumber,
            @QueryParam("per_page") Long pageSize,
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
        ResponseDto responseDto = customerService.getAllCustomerTransactions(pageNumber, pageSize, sortBy, sortDirection);
        return Response.ok(responseDto).build();

    }
    @GET
    @Path("/pocket-transfers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCustomerPocketTransfers(
            @QueryParam("page") Long pageNumber,
            @QueryParam("per_page") Long pageSize,
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
        ResponseDto responseDto = customerService.getAllCustomerPocketTransfers(pageNumber, pageSize, sortBy, sortDirection);
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
    @Path("/add-pockets")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addCustomerPockets(AssociationReqDto associationReqDto){
        return Response.ok(associationManager.addAssociation(null, Models.CUSTOMER, associationReqDto.getAssociationRefNos(), Models.POCKET)).build();
    }
    @Path("/add-pockets-dtos")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addCustomerPOCKETSDTOS(AssociationGenericReqDto<PocketReqDto> associationGenericReqDto){
        return Response.ok(associationManager.addAssociationDtos(null, Models.CUSTOMER, associationGenericReqDto.getAssociationReqDtos(), Models.POCKET)).build();
    }
    @Path("/remove-pockets")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeCustomerPockets(AssociationReqDto associationReqDto) {
        return Response.ok(associationManager.removeAssociation(null, Models.CUSTOMER, associationReqDto.getAssociationRefNos(), Models.POCKET)).build();
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
    @Path("/get-pockets")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomerPockets() {
        return Response.ok(customerService.getCustomerAssociation(Models.POCKET)).build();
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
