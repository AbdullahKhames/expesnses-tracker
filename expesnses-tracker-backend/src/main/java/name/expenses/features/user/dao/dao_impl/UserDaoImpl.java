package name.expenses.features.user.dao.dao_impl;

import jakarta.inject.Singleton;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import name.expenses.config.advice.RepoAdvice;
import name.expenses.error.exception.UsernameNotFoundException;
import name.expenses.features.user.dao.UserDao;
import name.expenses.features.user.dao.UserRepo;
import name.expenses.features.user.models.User;

import java.util.Optional;
import java.util.Set;

@Singleton
@Transactional
@Slf4j
@Interceptors(RepoAdvice.class)
public class UserDaoImpl implements UserDao, UserRepo {
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

    @Override
    public Optional<User> findUserByEmailAndDeletedIsFalse(String email) {
        return entityManager.createQuery(
                        "SELECT u FROM User u WHERE u.email = :email AND u.deleted = false", User.class)
                .setParameter("email", email)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public Optional<User> findUserByIdAndDeletedIsFalse(Long id) {
        return Optional.ofNullable(entityManager.find(User.class, id));
    }

    @Override
    public boolean existsByEmail(String email) {
        long count = entityManager.createQuery(
                        "SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class)
                .setParameter("email", email)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public boolean existsByEmailAndDeletedIsFalse(String email) {
        long count = entityManager.createQuery(
                        "SELECT COUNT(u) FROM User u WHERE u.email = :email AND u.deleted = false", Long.class)
                .setParameter("email", email)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public Set<User> findAllByDeletedIsFalse() {
        return Set.copyOf(entityManager.createQuery(
                        "SELECT u FROM User u WHERE u.deleted = false", User.class)
                .getResultList());
    }

    @Override
    public Optional<User> findByRef(String UUID) {
        return entityManager.createQuery(
                        "SELECT u FROM User u WHERE u.refNo = :UUID", User.class)
                .setParameter("UUID", UUID)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public Optional<User> findByRefAndDeletedIsFalse(String UUID) {
        return entityManager.createQuery(
                        "SELECT u FROM User u WHERE u.refNo = :UUID AND u.deleted = false", User.class)
                .setParameter("UUID", UUID)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public Optional<User> findUserByRefAndDeletedIsFalse(String refNo) {
        return entityManager.createQuery(
                        "SELECT u FROM User u WHERE u.refNo = :refNo AND u.deleted = false", User.class)
                .setParameter("refNo", refNo)
                .getResultList()
                .stream()
                .findFirst();
    }


    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(entityManager.find(User.class, id));
    }

    @Override
    public User save(User user) {
        if (user.getId() != null && entityManager.find(User.class, user.getId()) != null) {
            return entityManager.merge(user);
        } else {
            entityManager.persist(user);
            return user;
        }
    }
    @Override
    public User update(User user) {
        entityManager.merge(user);
        return user;
    }
}
