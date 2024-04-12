package name.expenses.features.user.service;

import jakarta.ejb.Local;
import name.expenses.error.exception.UsernameNotFoundException;
import name.expenses.features.user.models.User;

@Local
public interface UserDetailsService {
    User loadUserByUsername(String username) throws UsernameNotFoundException;
}