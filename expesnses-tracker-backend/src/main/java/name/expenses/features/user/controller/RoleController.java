package name.expenses.features.user.controller;

import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import name.expenses.config.AroundAdvice;
import name.expenses.features.user.service.RoleService;

@Path("/roles")
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Inject})
@Interceptors(AroundAdvice.class)
public class RoleController {
    private final RoleService roleService;
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(){
        return Response.ok(roleService.getAll()).build();
    }
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") Long id){
        return Response.ok(roleService.getById(id)).build();
    }
    @Path("/{roleName}")
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces(MediaType.APPLICATION_JSON)
    public Response addNewRole(@PathParam("roleName") String roleName) {
        return Response.ok(roleService.saveRole(roleName)).build();
    }
}