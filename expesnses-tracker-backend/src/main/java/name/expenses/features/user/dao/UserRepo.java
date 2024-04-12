package name.expenses.features.user.dao;

import jakarta.ejb.Local;
import name.expenses.features.user.models.User;

import java.util.Optional;
import java.util.Set;
@Local
public interface UserRepo {

    Optional<User> findUserByEmailAndDeletedIsFalse(String email);
    Optional<User> findUserByIdAndDeletedIsFalse(Long id);

    boolean existsByEmail(String email);
    boolean existsByEmailAndDeletedIsFalse(String email);

    Set<User> findAllByDeletedIsFalse();

//    Optional<User> findByDriver_DriverId(Long id);
    Optional<User> findByRef(String UUID);
    Optional<User> findByRefAndDeletedIsFalse(String UUID);


    Optional<User> findUserByRefAndDeletedIsFalse(String refNo);


    Optional<User> findById(Long id);

    User save(User user);

    User update(User user);
}