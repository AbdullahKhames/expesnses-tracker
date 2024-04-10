package name.expenses.features.user.service.service_impl;

import jakarta.ejb.Stateless;
import name.expenses.features.user.service.UserService;

@Stateless
public class UserServiceBean implements UserService {
    @Override
    public String getUserInfo() {
        return "User information retrieved from EJB";
    }
}