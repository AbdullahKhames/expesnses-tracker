package name.expenses.config.filters.validators;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import name.expenses.error.exception.ObjectNotFoundException;
import name.expenses.features.user.dao.TokenRepo;
import name.expenses.features.user.models.User;
import name.expenses.features.user.service.UserDetailsService;
import name.expenses.features.user.service.service_impl.JwtService;

@Slf4j
@Singleton
public class JWTAuthValidator {
    @Inject
    private JwtService jwtService;
    @Inject
    private TokenRepo tokenRepo;
    @Inject
    private UserDetailsService userDetailsService;

    public User bearerTokenValidation(String authToken) {
        String jwt = null;
        String username = null;
        try {
            jwt = authToken.substring(7);
            log.info("extract user name from token");

            username = jwtService.extractUsername(jwt);

        } catch (ExpiredJwtException expiredJwtException) {
            throw new RuntimeException("token is expired");

        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
        if (username != null && jwt != null) {

            User user;
            try {
                user = userDetailsService.loadUserByUsername(username);
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }

            if (user == null) {
                throw new ObjectNotFoundException("user is not found");
            }
            var isTokenValid = tokenRepo.findByToken(jwt)
                    .map(t -> !t.isExpired() && !t.isRevoked())
                    .orElse(false);

            try {
                if (jwtService.isTokenValid(jwt, user) && isTokenValid){
                    return user;
                }
                return null;
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }
        throw new RuntimeException("user name is not found");

    }
}
