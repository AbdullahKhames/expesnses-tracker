package name.expenses.features.user.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import name.expenses.config.advice.AroundAdvice;
import name.expenses.features.user.dtos.request.ChangeEmailRequest;
import name.expenses.features.user.dtos.request.LoginRequest;
import name.expenses.features.user.dtos.request.UserReqDto;
import name.expenses.features.user.dtos.request.UserRoleDto;
import name.expenses.features.user.service.UserService;
import name.expenses.features.user.service.service_impl.AuthService;
import name.expenses.features.user.service.service_impl.JwtService;

import java.io.IOException;

@Path("/users")
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Inject})
@Interceptors(AroundAdvice.class)
public class UserController {
    private final UserService userService;
    private final AuthService authService;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class AccessToken {
        String token;
    }

    @Path("/register")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response Register(UserReqDto userRegistrationDto) {
        return Response.ok(authService.register(userRegistrationDto, false)).build();
    }


    @Path("/login")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginRequest authRequest) {
        return Response.ok(authService.phoneLogin(authRequest)).build();
    }
    @Path("/refreshToken")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response refreshToken(
            @Context HttpServletRequest request,
             AccessToken accessToken
    ) throws IOException {

         return Response.ok(authService.refreshToken(request , accessToken.token)).build();
    }

    @Path("/resetAccount")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response resetAccount(LoginRequest loginRequest) {
        return Response.ok(authService.resetAccount(loginRequest)).build();
    }
    @RolesAllowed("ADMIN")
    @Path("/logout/{refNo}")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response logoutWithToken(@PathParam("refNo") String refNo) {
        return Response.ok(authService.logoutWithRef(refNo)).build();
    }

    @Path("/auth")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("ROLE_ADMIN")
    public String test() {
        return "user";
    }

    @Path("/{email}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam("email") String email) {
        return Response.ok(userService.getUser(email)).build();
    }

    @Path("/getById/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam("id") Long id) {
        return Response.ok(userService.getUserById(id)).build();
    }


    @Path("/addRole")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addRoleToUser(UserRoleDto userRoleDto) {
        return Response.ok(userService.addRoleToUser(userRoleDto)).build();
    }


    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") String refNo) {
        return Response.ok(userService.softDeleteUser(refNo)).build();
    }
    @Path("/all")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        return Response.ok(userService.getAll()).build();
    }

    @Path("/activate/{id}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response activateUser(@PathParam("id") Long id) {
        return Response.ok(userService.activateUser(id)).build();
    }

    @Path("/deactivate/{id}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deActivateUser(@PathParam("id") Long id) {
        return Response.ok(userService.deActivateUser(id)).build();
    }

    @Path("/testToken")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String testToken(@Context HttpServletRequest request) {
        JwtService jwtService = new JwtService();

        return jwtService.extractKeyFromRequest(request, "UUID");
    }
    @Path("/logout")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response logout(){
        return Response.ok(authService.logout()).build();
    }


    @Path("/validateChangeEmail")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response validateChangeEmail(ChangeEmailRequest changeEmailRequest) {

        return Response.ok(userService.validateChangeEmail(changeEmailRequest.getRefNo(), changeEmailRequest.getToken())).build();
    }

    @Path("/changeEmail")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response changeEmail(ChangeEmailRequest changeEmailRequest) {

        return Response.ok(userService.changeEmail(changeEmailRequest.getToken(), changeEmailRequest.getRefNo(), changeEmailRequest.getNewEmail())).build();
    }
    @Path("/getByRef/{refNo}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserByRef(@PathParam("refNo") String refNo) {
        return Response.ok(userService.getUserByRef(refNo)).build();
    }

}