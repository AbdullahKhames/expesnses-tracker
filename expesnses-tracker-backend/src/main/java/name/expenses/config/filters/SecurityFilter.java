package name.expenses.config.filters;

import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import name.expenses.error.exception.ErrorCode;
import name.expenses.error.exception_handler.models.ErrorCategory;
import name.expenses.error.exception_handler.models.ResponseError;
import name.expenses.features.user.models.UserDetails;
import name.expenses.features.user.service.UserDetailsService;
import name.expenses.features.user.service.service_impl.JwtService;
import name.expenses.utils.hashing.Hashing;

import java.io.IOException;
import java.util.*;
@Provider
public class SecurityFilter implements ContainerRequestFilter {
    @Inject
    private JwtService jwtService;

    @Inject
    private UserDetailsService userDetailsService;
//    @Inject
//    private ApiKeyRepo apiKeyRepo;
//    @Inject
//    private TokenRepo tokenRepo;
    private static final List<String> WHITELIST =new ArrayList<>(Arrays.asList(
            "/api/user/**",
            "/uploads/**",
            "/api/auth/**",
            "/api/appSettings/**",
            "/api/role/**",
            "/api/apikey/**",
            "/v3/api-docs/",
            "/v3/api-docs/**",
            "/swagger-resources**",
            "/swagger-ui**",
            "/swagger-ui/**"));

    private static final String AUTHORIZATION_HEADER_KEY = "Authorization";
    private static final String AUTHORIZATION_HEADER_BASIC_PREFIX = "Basic ";
    private static final String AUTHORIZATION_HEADER_JWT_PREFIX = "Bearer ";
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if (!WHITELIST.contains(requestContext.getUriInfo().getPath())){
            List<String > authHeader = requestContext.getHeaders().get(AUTHORIZATION_HEADER_KEY);
            if (authHeader != null && !authHeader.isEmpty()){
                String authToken = authHeader.get(0);
                authToken = authToken.replaceFirst(AUTHORIZATION_HEADER_BASIC_PREFIX, "");
                byte[] base64DecodedBytes = Base64.getDecoder().decode(authToken);
                String decodedString = new String(base64DecodedBytes);
                StringTokenizer stringTokenizer = new StringTokenizer(decodedString, ":");
                String userName = stringTokenizer.nextToken();
                String password = stringTokenizer.nextToken();
                UserDetails userDetails = null;
                try {
                    userDetails = userDetailsService.loadUserByUsername(userName);
                }catch (Exception exception){
                    errorResponse(requestContext, exception);
                }

                if (userDetails == null) {
                    errorResponse(requestContext, null);
                }
//            String[] mutableHash = new String[1];
//            Function<String, Boolean> update = hash -> { mutableHash[0] = hash; return true; };
//            Hashing.verifyAndUpdateHash(password, userDetails.getPassword(), update);


                if (Hashing.verify(password, userDetails.getPassword())){
                    return;
                }

            }
            errorResponse(requestContext, null);
        }

        final String jwt;
        final String username;
    }

    private static void errorResponse(ContainerRequestContext requestContext, Exception exception) {
        ResponseError responseError = new ResponseError();
        responseError.setErrorCategory(ErrorCategory.DATABASE_Error);
        if (exception != null && exception.getMessage()!= null) {
            responseError.setErrorMessage(exception.getMessage());
        }
        responseError.setErrorMessage("something went wrong user is not authorized");

        responseError.setErrorCode(ErrorCode.CST_IS_NOT_FOUND.getErrorCode());
        Response response = Response
                .status(Response.Status.UNAUTHORIZED)
                .entity(responseError)
                .build();

        requestContext.abortWith(response);
    }
}
