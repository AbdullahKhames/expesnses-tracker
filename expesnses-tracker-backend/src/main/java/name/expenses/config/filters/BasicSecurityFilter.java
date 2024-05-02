package name.expenses.config.filters;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import name.expenses.config.filters.validators.BasicAuthValidator;
import name.expenses.config.filters.validators.DeviceIdValidator;
import name.expenses.config.filters.validators.JWTAuthValidator;
import name.expenses.error.exception.ErrorCode;
import name.expenses.error.exception_handler.models.ErrorCategory;
import name.expenses.error.exception_handler.models.ResponseError;
import name.expenses.features.user.models.User;
import name.expenses.features.user.models.UserSecurityContext;
import name.expenses.utils.ResponseDtoBuilder;
import name.expenses.utils.property_loader.PropertyLoaderComponent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Provider
@Slf4j
@Priority(Priorities.AUTHENTICATION)
public class BasicSecurityFilter implements ContainerRequestFilter {
    @Inject
    private JWTAuthValidator jwtAuthValidator;
    @Inject
    private BasicAuthValidator basicAuthValidator;
    @Inject
    private DeviceIdValidator deviceIdValidator;
    @Inject
    private PropertyLoaderComponent propertyLoaderComponent;

    @PostConstruct
    private void init()
    {
//        AUTHORIZATION_HEADER_PREFIX = propertyLoaderComponent.getPropertyAsString("AUTHORIZATION_HEADER_PREFIX") + " ";
    }
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String DEVICE_ID_HEADER_KEY = propertyLoaderComponent.getPropertyAsString("Device_ID");
        String AUTHORIZATION_HEADER_PREFIX = propertyLoaderComponent.getPropertyAsString("AUTHORIZATION_HEADER_PREFIX") + " ";
        List<String> WHITELIST = new ArrayList<>(List.of(propertyLoaderComponent.getPropertyAsString("APIS.WHITELIST").split(",")));
        final String REQUEST_PATH = requestContext.getUriInfo().getAbsolutePath().getPath();
        final String METHOD = requestContext.getMethod();
        User user = null;
        Response response = null;
        if (!isPathInWhitelist(WHITELIST, REQUEST_PATH, METHOD)) {
            String AUTHORIZATION_HEADER_KEY = "Authorization";
            List<String> authHeader = deviceIdValidator.getHeaderKey(requestContext, AUTHORIZATION_HEADER_KEY);
            if (authHeader != null && !authHeader.isEmpty()) {
                String authToken = authHeader.get(0);
                if (AUTHORIZATION_HEADER_PREFIX.equals("Bearer ")){
                    try {
                        user = jwtAuthValidator.bearerTokenValidation(authToken);
                    }catch (Exception ex){
                        response = errorResponse(ex);
                        requestContext.abortWith(response);
                    }
                }else if (AUTHORIZATION_HEADER_PREFIX.equals("Basic ")){
                    try {
                        user = basicAuthValidator.basicAuthValidation(authToken, AUTHORIZATION_HEADER_PREFIX);
                    }catch (Exception ex){
                        response = errorResponse(ex);
                        requestContext.abortWith(response);
                    }
                }else {
                    response = errorResponse(new RuntimeException("AUTHORIZATION_HEADER_PREFIX not configured correctly please configure in the application.properties"));
                    requestContext.abortWith(response);
                }

            }else {
                response = errorResponse(new RuntimeException("auth header not found in request"));
                requestContext.abortWith(response);
            }

            if (user == null){
                response = errorResponse(new RuntimeException("user is invalid provided credentials doesn't match"));
                requestContext.abortWith(response);
            }
            UserSecurityContext userSecurityContext = new UserSecurityContext(user);
            requestContext.setSecurityContext(userSecurityContext);

            try {
                if ( user != null && !deviceIdValidator.validateDeviceId(requestContext, DEVICE_ID_HEADER_KEY, user)){
                    response = errorResponse(new IllegalArgumentException("device id provided in header doesn't match device id"));
                    requestContext.abortWith(response);
                }
//                if (performAuthorizationRules(userSecurityContext, REQUEST_PATH, METHOD)){
//                    return;
//                }
                performAuthorizationRules(userSecurityContext, REQUEST_PATH, METHOD);
            }catch (Exception exception){
                response = errorResponse(exception);
                requestContext.abortWith(response);
            }
            if (response == null) {
                return;
            }
            if (user == null){
                response = errorResponse(new RuntimeException("user is invalid provided credentials doesn't match"));
                requestContext.abortWith(response);
            }
            requestContext.abortWith(response);
        }
        // here it means it is in white list
    }



    private void performAuthorizationRules(UserSecurityContext userSecurityContext, String REQUEST_PATH, String METHOD) {
        try{
            List<String> ADMIN_APIS = new ArrayList<>(List.of(propertyLoaderComponent.getPropertyAsString("ADMIN_APIS").split(",")));
            List<String> CUSTOMER_APIS = new ArrayList<>(List.of(propertyLoaderComponent.getPropertyAsString("CUSTOMER_APIS").split(",")));
            List<String> SHARED_APIS = new ArrayList<>(List.of(propertyLoaderComponent.getPropertyAsString("SHARED_APIS").split(",")));

            if (isPathInWhitelist(SHARED_APIS, REQUEST_PATH, METHOD)){
                List<String> SHARED_ROLES = new ArrayList<>(List.of(propertyLoaderComponent.getPropertyAsString("SHARED_ROLES").split(",")));
                if (SHARED_ROLES.stream().anyMatch(userSecurityContext::isUserInRole)) {
                    return;
                }else {
                    throw new RuntimeException("you are not authorized to use this request");
                }
            }else if (isPathInWhitelist(ADMIN_APIS, REQUEST_PATH, METHOD)){
                if (userSecurityContext.isUserInRole("ROLE_ADMIN")) {
                    return;
                }else {
                    throw new RuntimeException("you are not authorized to use this request ADMIN ONLY");
                }
            }else if(isPathInWhitelist(CUSTOMER_APIS, REQUEST_PATH, METHOD)){
                if (userSecurityContext.isUserInRole("ROLE_CUSTOMER")) {
                    return;
                }else {
                    throw new RuntimeException("you are not authorized to use this request CUSTOMER ONLY");
                }
            }else {
                log.info("no authorization rules for this endpoint");
            }
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }

    }

    private static Response errorResponse(Exception exception) {
        ResponseError responseError = new ResponseError();
        responseError.setErrorCategory(ErrorCategory.BusinessError);
        if (exception != null && exception.getMessage()!= null) {
            responseError.setErrorMessage(exception.getMessage());
        }else{
            responseError.setErrorMessage("something went wrong user is not authorized");
        }
        responseError.setErrorCode(ErrorCode.CST_IS_NOT_FOUND.getErrorCode());

        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(ResponseDtoBuilder.getErrorResponse(810, responseError))
                .build();
    }
    public boolean isPathInWhitelist(List<String> WHITELIST, String path, String method) {
        for (String pattern : WHITELIST) {
            String[] parts = pattern.split("\\s+"); // Split the pattern into URI and method
            if (parts.length != 2) {
                // Invalid whitelist entry, skip
                continue;
            }
            String uriPattern = parts[0];
            String methodPattern = parts[1];
            if (uriPattern.endsWith("/**")) {
                uriPattern = uriPattern.substring(0, uriPattern.length() - 3) + ".*";
            }
            // Convert the URI pattern to a regular expression
//            String uriRegex = uriPattern.replaceAll("\\*", ".*");
            // Check if the request path matches the URI pattern
            if (!Pattern.matches(uriPattern, path)) {
                // URI doesn't match, move to the next pattern
                continue;
            }

            // Check if the method matches the method pattern
            if (methodPattern.equals("*") || methodPattern.equalsIgnoreCase(method)) {
                // Method matches, return true
                return true;
            }
        }
        return false;
    }
}
