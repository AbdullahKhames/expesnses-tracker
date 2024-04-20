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
        String userDeviceId = getUserDeviceId(user);
        List<String> headerDeviceId = getHeaderKey(requestContext, key);

        if (headerDeviceId == null || userDeviceId == null) {
            throw new ObjectNotFoundException(String.format("the user device id cannot be null and the header device id cannot be null %s", headerDeviceId));
        }

        if (!Objects.equals(userDeviceId, headerDeviceId.get(0))){
            throw new ObjectNotFoundException(String.format("the user device id and the header device id %s must be equal", headerDeviceId));
        }
        return true;
    }
}
