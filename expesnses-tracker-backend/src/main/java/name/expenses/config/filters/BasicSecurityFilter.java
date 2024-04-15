package name.expenses.config.filters;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import name.expenses.error.exception.ErrorCode;
import name.expenses.error.exception_handler.models.ErrorCategory;
import name.expenses.error.exception_handler.models.ResponseError;
import name.expenses.features.user.dao.TokenRepo;
import name.expenses.features.user.models.User;
import name.expenses.features.user.models.UserDetails;
import name.expenses.features.user.models.UserSecurityContext;
import name.expenses.features.user.service.UserDetailsService;
import name.expenses.features.user.service.service_impl.JwtService;
import name.expenses.utils.ResponseDtoBuilder;
import name.expenses.utils.hashing.Hashing;
import name.expenses.utils.property_loader.PropertyLoaderComponent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

@Provider
@Slf4j
@Priority(Priorities.AUTHENTICATION)
public class BasicSecurityFilter implements ContainerRequestFilter {
    @Inject
    private JwtService jwtService;
    @Inject
    private TokenRepo tokenRepo;
    @Inject
    private UserDetailsService userDetailsService;
    @Inject
    private PropertyLoaderComponent propertyLoaderComponent;

    private String AUTHORIZATION_HEADER_PREFIX;
    @PostConstruct
    private void init()
    {
//        AUTHORIZATION_HEADER_PREFIX = propertyLoaderComponent.getPropertyAsString("AUTHORIZATION_HEADER_PREFIX") + " ";
    }
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        AUTHORIZATION_HEADER_PREFIX = propertyLoaderComponent.getPropertyAsString("AUTHORIZATION_HEADER_PREFIX") + " ";
        List<String> WHITELIST = new ArrayList<>(List.of(propertyLoaderComponent.getPropertyAsString("APIS.WHITELIST").split(",")));
        final String REQUEST_PATH = requestContext.getUriInfo().getAbsolutePath().getPath();
        final String METHOD = requestContext.getMethod();
        if (!isPathInWhitelist(WHITELIST, REQUEST_PATH, METHOD)) {
            String AUTHORIZATION_HEADER_KEY = "Authorization";
            List<String> authHeader = requestContext.getHeaders().get(AUTHORIZATION_HEADER_KEY);
            if (authHeader != null && !authHeader.isEmpty()) {
                String authToken = authHeader.get(0);
                if (AUTHORIZATION_HEADER_PREFIX.equals("Bearer ")){
                    String jwt = null;
                    String username = null;
                    try {
                        jwt = authToken.substring(7);
                        log.info("extract user name from token");

                        username = jwtService.extractUsername(jwt);

                    }catch (ExpiredJwtException expiredJwtException){
                        Response response  = errorResponse(requestContext, new RuntimeException("token is expired"));
                                requestContext.abortWith(response);

                    } catch (Exception exception){
                        Response response  = errorResponse(requestContext, exception);
                                requestContext.abortWith(response);

                    }
                    if(username != null && jwt != null){
                        User user = null;
                        try {
                            user = userDetailsService.loadUserByUsername(username);
                        } catch (Exception exception) {
                            Response response  = errorResponse(requestContext, exception);
                                    requestContext.abortWith(response);

                        }

                        if (user == null) {
                            Response response  = errorResponse(requestContext, null);
                                    requestContext.abortWith(response);

                        }
                        var isTokenValid = tokenRepo.findByToken(jwt)
                                .map(t -> !t.isExpired() && !t.isRevoked())
                                .orElse(false);

                        try{
                            if (jwtService.isTokenValid(jwt , user) && isTokenValid){
                                if (performAuthorizationRules(requestContext, user, REQUEST_PATH, METHOD)){
                                    return;
                                }else {
                                    Response response  = errorResponse(requestContext, null);
                                            requestContext.abortWith(response);

                                }
                            }else {
                                Response response  = errorResponse(requestContext, null);
                                        requestContext.abortWith(response);

                            }
                        }catch (Exception exception){
                            Response response  = errorResponse(requestContext, exception);
                                    requestContext.abortWith(response);

                        }

                    }
                }else if (AUTHORIZATION_HEADER_PREFIX.equals("Basic ")){
                    String userName = null;
                    String password = null;
                    try{
                        authToken = authToken.replaceFirst(AUTHORIZATION_HEADER_PREFIX, "");
                        byte[] base64DecodedBytes = Base64.getDecoder().decode(authToken);
                        String decodedString = new String(base64DecodedBytes);
                        StringTokenizer stringTokenizer = new StringTokenizer(decodedString, ":");
                        userName = stringTokenizer.nextToken();
                        password = stringTokenizer.nextToken();
                    }catch (Exception exception){
                        Response response  = errorResponse(requestContext, exception);
                                requestContext.abortWith(response);

                    }
                    if (userName == null || password == null) {
                        Response response  = errorResponse(requestContext, null);
                                requestContext.abortWith(response);

                    }
                    User user = null;
                    try {
                        user = userDetailsService.loadUserByUsername(userName);
                    } catch (Exception exception) {
                        Response response  = errorResponse(requestContext, exception);
                                requestContext.abortWith(response);

                    }

                    if (user == null) {
                        Response response  = errorResponse(requestContext, null);
                                requestContext.abortWith(response);

                    }

                    // if the password is correct check for authorization roles here
                    try {
                        if (Hashing.verify(password, user.getPassword())) {
                            if (performAuthorizationRules(requestContext, user, REQUEST_PATH, METHOD)){
                                return;
                            }else {
                                Response response  = errorResponse(requestContext, null);
                                        requestContext.abortWith(response);

                            }
                        }
                    }catch (Exception ex){
                        Response response  = errorResponse(requestContext, ex);
                                requestContext.abortWith(response);

                    }

                }

            }
            // no auth header found
            Response response  = errorResponse(requestContext, null);
                    requestContext.abortWith(response);

        }
        // here it means it is in white list
    }

    private boolean performAuthorizationRules(ContainerRequestContext requestContext, User user, String REQUEST_PATH, String METHOD) {
        // Add authenticated user to the request context
        try{
            UserSecurityContext userSecurityContext = new UserSecurityContext(user);
            requestContext.setSecurityContext(userSecurityContext);
            List<String> ADMIN_APIS = new ArrayList<>(List.of(propertyLoaderComponent.getPropertyAsString("ADMIN_APIS").split(",")));
            List<String> CUSTOMER_APIS = new ArrayList<>(List.of(propertyLoaderComponent.getPropertyAsString("CUSTOMER_APIS").split(",")));
            List<String> SHARED_APIS = new ArrayList<>(List.of(propertyLoaderComponent.getPropertyAsString("SHARED_APIS").split(",")));

            if (isPathInWhitelist(SHARED_APIS, REQUEST_PATH, METHOD)){
                List<String> SHARED_ROLES = new ArrayList<>(List.of(propertyLoaderComponent.getPropertyAsString("SHARED_ROLES").split(",")));
                if (SHARED_ROLES.stream().anyMatch(userSecurityContext::isUserInRole)) {
                    return true;
                }else {
                    Response response  = errorResponse(requestContext, new RuntimeException("you are not authorized to use this request"));
                            requestContext.abortWith(response);

                }
            }else if (isPathInWhitelist(ADMIN_APIS, REQUEST_PATH, METHOD)){
                if (userSecurityContext.isUserInRole("ROLE_ADMIN")) {
                    return true;
                }else {
                    Response response  = errorResponse(requestContext, new RuntimeException("you are not authorized to use this request ADMIN ONLY"));
                            requestContext.abortWith(response);

                }
            }else if(isPathInWhitelist(CUSTOMER_APIS, REQUEST_PATH, METHOD)){
                if (userSecurityContext.isUserInRole("ROLE_CUSTOMER")) {
                    return true;
                }else {
                    Response response  = errorResponse(requestContext, new RuntimeException("you are not authorized to use this request CUSTOMER ONLY"));
                            requestContext.abortWith(response);

                }
            }
            // here means that there is no authorization rule for this api (end point)
//                    else{
//
//                    }
            return true;
        }catch (Exception ex){
            return false;
        }

    }

    private static Response errorResponse(ContainerRequestContext requestContext, Exception exception) {
        ResponseError responseError = new ResponseError();
        responseError.setErrorCategory(ErrorCategory.BusinessError);
        if (exception != null && exception.getMessage()!= null) {
            responseError.setErrorMessage(exception.getMessage());
        }else{
            responseError.setErrorMessage("something went wrong user is not authorized");
        }
        responseError.setErrorCode(ErrorCode.CST_IS_NOT_FOUND.getErrorCode());

        Response response = Response
                .status(Response.Status.UNAUTHORIZED)
                .entity(ResponseDtoBuilder.getErrorResponse(810, responseError))
                .build();

        return response;
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
