package name.expenses.features.user.models;


import jakarta.ws.rs.core.SecurityContext;

import java.security.Principal;

public class UserSecurityContext implements SecurityContext {
    private final User user;

    public UserSecurityContext(User user) {
        this.user = user;
    }

    @Override
    public Principal getUserPrincipal() {
        return user;
    }

    @Override
    public boolean isUserInRole(String role) {
        return user
                .getRoles()
                .stream()
                .map(Role::getName)
                .anyMatch(roleName -> roleName.equalsIgnoreCase(role));
    }

    public User getUser(){
        return this.user;
    }
    @Override
    public boolean isSecure() {
        // Implement logic to determine if the request is secure
        return true; // For example, always return true if all requests are secure
    }

    @Override
    public String getAuthenticationScheme() {
        return SecurityContext.BASIC_AUTH;
    }
}