package name.expenses.features.user.dao;

import jakarta.ejb.Local;
import name.expenses.features.user.models.User;

import java.util.Optional;

@Local
public interface UserDao {
    Optional<User> findUserByUsernameAndDeletedIsFalse(String userName);

}
