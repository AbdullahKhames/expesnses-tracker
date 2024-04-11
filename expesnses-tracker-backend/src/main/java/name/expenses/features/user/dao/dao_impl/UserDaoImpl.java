package name.expenses.features.user.dao.dao_impl;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import name.expenses.error.exception.GeneralFailureException;
import name.expenses.error.exception.UsernameNotFoundException;
import name.expenses.features.user.dao.UserDao;
import name.expenses.features.user.models.User;

import java.util.Map;
import java.util.Optional;

@Singleton
@Transactional
@Slf4j
public class UserDaoImpl implements UserDao {
    @PersistenceContext(unitName = "expenses-unit")
    private EntityManager entityManager;

    public Optional<User> findUserByUsernameAndDeletedIsFalse(String userName){
        if (userName == null) {
            return Optional.empty();
        }
        try{
            TypedQuery<User> userTypedQuery = entityManager.createQuery("SELECT u FROM User u WHERE u.email = :userName AND u.deleted is false", User.class);
            userTypedQuery.setParameter("userName", userName);
            return Optional.ofNullable(userTypedQuery.getSingleResult());
        }catch (Exception ex){
            throw new UsernameNotFoundException(String.format("user with email %s is not found !", userName));
        }
    }
}
