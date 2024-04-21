package name.expenses.features.pocket.controller;

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
import name.expenses.features.pocket.dtos.request.PocketReqDto;
import name.expenses.features.pocket.dtos.request.PocketUpdateDto;
import name.expenses.features.pocket.service.PocketService;
import name.expenses.globals.SortDirection;
import name.expenses.globals.responses.ResponseDto;

@Path("/pockets")
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Inject})
@Interceptors(AroundAdvice.class)
public class PocketController {
    private final PocketService pocketService;
    private final ResponseExceptionBuilder responseExceptionBuilder;

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces(MediaType.APPLICATION_JSON)
    public Response createPocket(String rawRequestBody){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            PocketReqDto request = objectMapper.readValue(rawRequestBody, PocketReqDto.class);
            return Response.ok(pocketService.create(request)).build();
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
    public Response getPocket(@PathParam("refNo") String refNo) {
        ResponseDto responseDto = pocketService.get(refNo);
        if (responseDto != null) {
            return Response.ok(responseDto).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
    @GET
    @Path("/name/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPocketByName(@PathParam("name") String name) {
        ResponseDto responseDto = pocketService.getPocketByName(name);
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
    public Response updatePocket(@PathParam("refNo") String refNo, String rawRequestBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            PocketUpdateDto request = objectMapper.readValue(rawRequestBody, PocketUpdateDto.class);
            return Response.ok(pocketService.update(refNo, request)).build();
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
    public Response deletePocket(@PathParam("refNo") String refNo) {
        ResponseDto responseDto = pocketService.delete(refNo);
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
        ResponseDto responseDto = pocketService.getAllEntities(pageNumber, pageSize, sortBy, sortDirection);
        return Response.ok(responseDto).build();

    }
    @GET
    @Path("/noAccount")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllEntitiesWithoutAccount(
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
        ResponseDto responseDto = pocketService.getAllEntitiesWithoutAccount(pageNumber, pageSize, sortBy, sortDirection);
        return Response.ok(responseDto).build();

    }
}
