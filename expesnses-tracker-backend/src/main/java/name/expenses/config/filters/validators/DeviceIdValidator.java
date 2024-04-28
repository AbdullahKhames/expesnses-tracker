package name.expenses.config.filters.validators;

import jakarta.inject.Singleton;
import jakarta.ws.rs.container.ContainerRequestContext;
import lombok.extern.slf4j.Slf4j;
import name.expenses.error.exception.ObjectNotFoundException;
import name.expenses.features.user.models.User;

import java.util.List;
import java.util.Objects;
@Slf4j
@Singleton
public class DeviceIdValidator {

    private String getUserDeviceId(User user){
        return user.getDeviceId();
    }
    public List<String> getHeaderKey(ContainerRequestContext requestContext, String key){
        return requestContext.getHeaders().get(key);
    }

    public boolean validateDeviceId(ContainerRequestContext requestContext, String key, User user){
        List<String> headerDeviceId = getHeaderKey(requestContext, key);

        if (headerDeviceId == null) {
            throw new ObjectNotFoundException("the header device id cannot be null");
        }
        String userDeviceId = getUserDeviceId(user);
        if ( userDeviceId == null) {
            throw new ObjectNotFoundException("the userDeviceId cannot be null");
        }

        if (!Objects.equals(userDeviceId, headerDeviceId.getFirst())){
            throw new ObjectNotFoundException(String.format("the user device id and the header device id %s must be equal", headerDeviceId));
        }
        return true;
    }
}
