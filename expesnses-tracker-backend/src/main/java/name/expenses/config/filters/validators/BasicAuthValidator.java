package name.expenses.config.filters.validators;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import name.expenses.error.exception.ObjectNotFoundException;
import name.expenses.features.user.models.User;
import name.expenses.features.user.service.UserDetailsService;
import name.expenses.utils.hashing.Hashing;

import java.util.Base64;
import java.util.StringTokenizer;
@Slf4j
@Singleton
public class BasicAuthValidator {
    @Inject
    private UserDetailsService userDetailsService;
    public User basicAuthValidation(String authToken, String AUTHORIZATION_HEADER_PREFIX) {
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
            throw new RuntimeException(exception);
        }
        if (userName == null || password == null) {
            throw new IllegalArgumentException("provided username and password cannot be null");
        }
        User user;
        try {
            user = userDetailsService.loadUserByUsername(userName);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }

        if (user == null) {
            throw new ObjectNotFoundException("user is not found");
        }

        // if the password is correct check for authorization roles here
        try {
            if (Hashing.verify(password, user.getPassword())){
                return user;
            }
            return null;
        }catch (Exception exception){
            throw new RuntimeException(exception);
        }
    }
}
