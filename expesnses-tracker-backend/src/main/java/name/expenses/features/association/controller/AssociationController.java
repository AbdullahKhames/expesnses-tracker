package name.expenses.features.association.controller;

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
import name.expenses.features.budget.dtos.request.BudgetReqDto;
import name.expenses.features.expesnse.dtos.request.ExpenseReqDto;

@Path("/association")
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Inject})
@Interceptors(AroundAdvice.class)
public class AssociationController {

    private final AssociationManager associationManager;
//    @Context
//    private SecurityContext securityContext;
//
//    private String getCustomerRef(){
//        try {
//            UserSecurityContext userSecurityContext = (UserSecurityContext) securityContext;
//            User user  = userSecurityContext.getUser();
//            return user.getRefNo();
//        }catch (Exception ex){
//            return null;
//        }
//    }
    @Path("/customers/{customerRef}/add-accounts")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addCustomerAccounts(@PathParam("customerRef") String customerRef, AssociationReqDto associationReqDto){
        return Response.ok(associationManager.addAssociation(customerRef, Models.CUSTOMER, associationReqDto.getAssociationRefNos(), Models.ACCOUNT)).build();
    }
    @Path("/customers/{customerRef}/remove-accounts")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeCustomerAccounts(@PathParam("customerRef") String customerRef, AssociationReqDto associationReqDto){
        return Response.ok(associationManager.removeAssociation(customerRef, Models.CUSTOMER, associationReqDto.getAssociationRefNos(), Models.ACCOUNT)).build();
    }
    @Path("/customers/{customerRef}/add-categories")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addCustomerCategories(@PathParam("customerRef") String customerRef, AssociationReqDto associationReqDto){
        return Response.ok(associationManager.addAssociation(customerRef, Models.CUSTOMER, associationReqDto.getAssociationRefNos(), Models.CATEGORY)).build();
    }
    @Path("/customers/{customerRef}/remove-categories")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeCustomerCategories(@PathParam("customerRef") String customerRef, AssociationReqDto associationReqDto){
        return Response.ok(associationManager.removeAssociation(customerRef, Models.CUSTOMER, associationReqDto.getAssociationRefNos(), Models.CATEGORY)).build();
    }
    @Path("/customers/{customerRef}/add-sub-categories")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addCustomerSubCategories(@PathParam("customerRef") String customerRef, AssociationReqDto associationReqDto){
        return Response.ok(associationManager.addAssociation(customerRef, Models.CUSTOMER, associationReqDto.getAssociationRefNos(), Models.SUB_CATEGORY)).build();
    }
    @Path("/customers/{customerRef}/remove-sub-categories")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeCustomerSubCategories(@PathParam("customerRef") String customerRef, AssociationReqDto associationReqDto){
        return Response.ok(associationManager.removeAssociation(customerRef, Models.CUSTOMER, associationReqDto.getAssociationRefNos(), Models.SUB_CATEGORY)).build();
    }
    @Path("/customers/{customerRef}/add-expenses")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addCustomerExpenses(@PathParam("customerRef") String customerRef, AssociationReqDto associationReqDto){
        return Response.ok(associationManager.addAssociation(customerRef, Models.CUSTOMER, associationReqDto.getAssociationRefNos(), Models.EXPENSE)).build();
    }
    @Path("/customers/{customerRef}/add-expenses-dtos")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addCustomerExpensesDTOS(@PathParam("customerRef") String customerRef, AssociationGenericReqDto<ExpenseReqDto> associationGenericReqDto){
        return Response.ok(associationManager.addAssociationDtos(customerRef, Models.CUSTOMER, associationGenericReqDto.getAssociationReqDtos(), Models.EXPENSE)).build();
    }

    @Path("/customers/{customerRef}/remove-expenses")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeCustomerExpenses(@PathParam("customerRef") String customerRef, AssociationReqDto associationReqDto){
        return Response.ok(associationManager.removeAssociation(customerRef, Models.CUSTOMER, associationReqDto.getAssociationRefNos(), Models.EXPENSE)).build();
    }
    @Path("/customers/{customerRef}/add-budgets")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addCustomerBudgets(@PathParam("customerRef") String customerRef, AssociationReqDto associationReqDto){
        return Response.ok(associationManager.addAssociation(customerRef, Models.CUSTOMER, associationReqDto.getAssociationRefNos(), Models.Budget)).build();
    }
    @Path("/customers/{customerRef}/add-budgets-dtos")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addCustomerbudgetSDTOS(@PathParam("customerRef") String customerRef, AssociationGenericReqDto<BudgetReqDto> associationGenericReqDto){
        return Response.ok(associationManager.addAssociationDtos(customerRef, Models.CUSTOMER, associationGenericReqDto.getAssociationReqDtos(), Models.Budget)).build();
    }
    @Path("/customers/{customerRef}/remove-budgets")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeCustomerBudgets(@PathParam("customerRef") String customerRef, AssociationReqDto associationReqDto) {
        return Response.ok(associationManager.removeAssociation(customerRef, Models.CUSTOMER, associationReqDto.getAssociationRefNos(), Models.Budget)).build();
    }

    @Path("/accounts/{accountRef}/add-budgets")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addAccountBudgets(@PathParam("accountRef") String accountRef, AssociationReqDto associationReqDto){
        return Response.ok(associationManager.addAssociation(accountRef, Models.ACCOUNT, associationReqDto.getAssociationRefNos(), Models.Budget)).build();
    }
    @Path("/accounts/{accountRef}/remove-budgets")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeAccountBudgets(@PathParam("accountRef") String accountRef, AssociationReqDto associationReqDto){
        return Response.ok(associationManager.removeAssociation(accountRef, Models.ACCOUNT, associationReqDto.getAssociationRefNos(), Models.Budget)).build();
    }
    @Path("/categories/{categoryRef}/add-sub-categories")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addCategorySubCategories(@PathParam("categoryRef") String categoryRef, AssociationReqDto associationReqDto){
        return Response.ok(associationManager.addAssociation(categoryRef, Models.CATEGORY, associationReqDto.getAssociationRefNos(), Models.SUB_CATEGORY)).build();
    }
    @Path("/categories/{categoryRef}/remove-sub-categories")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeCategorySubCategories(@PathParam("categoryRef") String categoryRef, AssociationReqDto associationReqDto){
        return Response.ok(associationManager.removeAssociation(categoryRef, Models.CATEGORY, associationReqDto.getAssociationRefNos(), Models.SUB_CATEGORY)).build();
    }
    @Path("/sub-categories/{subCategoryRef}/add-expenses")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addSubCategoryExpenses(@PathParam("subCategoryRef") String subCategoryRef, AssociationReqDto associationReqDto){
        return Response.ok(associationManager.addAssociation(subCategoryRef, Models.SUB_CATEGORY, associationReqDto.getAssociationRefNos(), Models.EXPENSE)).build();
    }
    @Path("/sub-categories/{subCategoryRef}/remove-expenses")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeSubCategoryExpenses(@PathParam("subCategoryRef") String subCategoryRef, AssociationReqDto associationReqDto){
        return Response.ok(associationManager.removeAssociation(subCategoryRef, Models.SUB_CATEGORY, associationReqDto.getAssociationRefNos(), Models.EXPENSE)).build();
    }

}
