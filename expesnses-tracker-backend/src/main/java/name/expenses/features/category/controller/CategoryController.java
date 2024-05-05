package name.expenses.features.category.controller;

import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import name.expenses.config.advice.AroundAdvice;
import name.expenses.features.category.dtos.request.CategoryReqDto;
import name.expenses.features.category.dtos.request.CategoryUpdateDto;
import name.expenses.features.category.service.CategoryService;
import name.expenses.globals.SortDirection;
import name.expenses.globals.responses.ResponseDto;
import name.expenses.utils.PageUtil;

@Path("/categories")
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Inject})
@Interceptors(AroundAdvice.class)
public class CategoryController {
    private final CategoryService expenseService;
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCategory(CategoryReqDto expense){
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
    public Response getCategory(@PathParam("refNo") String refNo) {
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
    public Response getCategoryByName(@PathParam("name") String name) {
        ResponseDto responseDto = expenseService.getCategoryByName(name);
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
    public Response updateCategory(@PathParam("refNo") String refNo, CategoryUpdateDto expenseUpdateDto) {
        ResponseDto responseDto = expenseService.update(refNo, expenseUpdateDto);
        return Response.ok(responseDto).build();
    }
    @GET
    @Path("/{refNo}/subCategories")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSubcategories(@PathParam("refNo") String refNo,
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
        SortDirection sortDirection = PageUtil.getSortDirection(direction);
        ResponseDto responseDto = expenseService
                .getSubcategories(refNo,
                        pageNumber,
                        pageSize,
                        sortBy,
                        sortDirection);
        return Response.ok(responseDto).build();
    }

    @PUT
    @Path("/addAssociation/{categoryRefNo}/{subCategoryRefNo}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAssociation(@PathParam("categoryRefNo") String categoryRefNo, @PathParam("subCategoryRefNo") String subCategoryRefNo) {
        ResponseDto responseDto = expenseService.addAssociation(categoryRefNo, subCategoryRefNo);
        return Response.ok(responseDto).build();
    }
    @PUT
    @Path("/removeAssociation/{categoryRefNo}/{subCategoryRefNo}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeAssociation(@PathParam("categoryRefNo") String categoryRefNo, @PathParam("subCategoryRefNo") String subCategoryRefNo) {
        ResponseDto responseDto = expenseService.removeAssociation(categoryRefNo, subCategoryRefNo);
        return Response.ok(responseDto).build();
    }

    @DELETE
    @Path("/{refNo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCategory(@PathParam("refNo") String refNo) {
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
}
