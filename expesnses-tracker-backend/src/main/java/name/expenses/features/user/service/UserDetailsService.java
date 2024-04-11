package name.expenses.features.user.service;

import jakarta.ejb.Local;
import name.expenses.error.exception.UsernameNotFoundException;
import name.expenses.features.user.models.UserDetails;
@Local
public interface UserDetailsService {
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}