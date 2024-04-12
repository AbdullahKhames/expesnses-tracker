package name.expenses.features.user.service.service_impl;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import name.expenses.error.exception.UsernameNotFoundException;
import name.expenses.features.user.dao.UserDao;
import name.expenses.features.user.models.User;
import name.expenses.features.user.service.UserDetailsService;

import java.util.Optional;

@Singleton
@Transactional
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserDao userDao;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userDao.findUserByUsernameAndDeletedIsFalse(username);
        if (userOptional.isPresent()){
            return userOptional.get();
        }
        throw new UsernameNotFoundException(String.format("user with email %s is not found !", username));
    }
}
