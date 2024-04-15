package name.expenses.features.sub_category.controller;

import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import name.expenses.config.advice.AroundAdvice;
import name.expenses.features.sub_category.dtos.request.SubCategoryReqDto;
import name.expenses.features.sub_category.dtos.request.SubCategoryUpdateDto;
import name.expenses.features.sub_category.service.SubService;
import name.expenses.globals.SortDirection;
import name.expenses.globals.responses.ResponseDto;

@Path("/sub-categories")
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Inject})
@Interceptors(AroundAdvice.class)
public class SubCategoryController {
    private final SubService expenseService;
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSubCategory(SubCategoryReqDto expense){
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
    public Response getSubCategory(@PathParam("refNo") String refNo) {
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
    public Response updateSubCategory(@PathParam("refNo") String refNo, SubCategoryUpdateDto expenseUpdateDto) {
        ResponseDto responseDto = expenseService.update(refNo, expenseUpdateDto);
        return Response.ok(responseDto).build();
    }

    @DELETE
    @Path("/{refNo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteSubCategory(@PathParam("refNo") String refNo) {
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
