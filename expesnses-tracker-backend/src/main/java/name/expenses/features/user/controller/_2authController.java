package name.expenses.features.user.controller;

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
import name.expenses.features.user.dtos.request.ValidAuthDto;
import name.expenses.features.user.dtos.request._2authDto;
import name.expenses.features.user.service._2authServices;
@Path("/otp")
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Inject})
@Interceptors(AroundAdvice.class)
public class _2authController {

    private final _2authServices authServices;
    private final ResponseExceptionBuilder responseExceptionBuilder;

    @Path("/sendOtp")
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendOtp(String rawRequestBody) {
        return getResponse(rawRequestBody);
    }

   @Path("/resendOtp")
   @POST
   @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   @Produces(MediaType.APPLICATION_JSON)
    public Response resendOtp(String rawRequestBody){
       return getResponse(rawRequestBody);
   }

    private Response getResponse(String rawRequestBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            _2authDto request = objectMapper.readValue(rawRequestBody, _2authDto.class);
            return Response.ok(authServices.add_2auth(request)).build();
        } catch (Exception e) {
            // Handle the deserialization error here...
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(responseExceptionBuilder.buildResponse(e))
                    .build();
        }
    }

    @Path("/Verification")
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces(MediaType.APPLICATION_JSON)
    public Response verification( ValidAuthDto authDto){
        return  Response.ok(authServices.codeVerification(authDto)).build();
    }

    @Path("test/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public boolean test(@PathParam("id") int id){
        return true;
    }
}