package name.expenses.features.user.dao.dao_impl;

import jakarta.inject.Singleton;
import jakarta.interceptor.Interceptors;
import jakarta.transaction.Transactional;
import name.expenses.config.advice.RepoAdvice;
import name.expenses.features.user.dao._2authRepo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import name.expenses.features.user.models._2auth;

import java.util.Optional;

@Singleton
@Interceptors(RepoAdvice.class)
@Transactional
public class _2authRepoImpl implements _2authRepo {

    @PersistenceContext(unitName = "expenses-unit")
    private EntityManager entityManager;

    @Override
    public Optional<_2auth> findByPhoneAndExpiredFalse(String email) {
        return entityManager.createQuery(
                "SELECT a FROM _2auth a WHERE a.email = :email AND a.expired = false", _2auth.class)
                .setParameter("email", email)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public _2auth findByPhoneAndOtp(String email, String otp) {
        return entityManager.createQuery(
                "SELECT a FROM _2auth a WHERE a.email = :email AND a.otp = :otp", _2auth.class)
                .setParameter("email", email)
                .setParameter("otp", otp)
                .getSingleResult();
    }

    @Override
    public boolean existsByPhoneAndTokenAndRfeNoAndDeviceId(String email, String token, String refNo, String deviceId) {
        long count = entityManager.createQuery(
                "SELECT COUNT(a) FROM _2auth a WHERE a.email = :email AND a.token = :token AND a.refNo = :refNo AND a.deviceId = :deviceId", Long.class)
                .setParameter("email", email)
                .setParameter("token", token)
                .setParameter("refNo", refNo)
                .setParameter("deviceId", deviceId)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public _2auth findByPhoneAndOtpAndRfeNoAndExpiredFalse(String email, String otp, String refNo) {
        return entityManager.createQuery(
                "SELECT a FROM _2auth a WHERE a.email = :email AND a.otp = :otp AND a.refNo = :refNo AND a.expired = false", _2auth.class)
                .setParameter("email", email)
                .setParameter("otp", otp)
                .setParameter("refNo", refNo)
                .getSingleResult();
    }

    @Override
    public Boolean existsByPhoneAndExpiredFalse(String email) {
        long count = entityManager.createQuery(
                "SELECT COUNT(a) FROM _2auth a WHERE a.email = :email AND a.expired = false", Long.class)
                .setParameter("email", email)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public _2auth save(_2auth auth) {
        if (auth.getId() != null && entityManager.find(_2auth.class, auth.getId()) != null) {
            return entityManager.merge(auth);
        } else {
            entityManager.persist(auth);
            return auth;
        }
    }

    @Override
    public void delete(_2auth auth) {
        entityManager.remove(auth);
    }

    @Override
    public Optional<_2auth> findById(Long id) {
        return Optional.ofNullable(entityManager.find(_2auth.class, id));
    }

    @Override
    public boolean existsByEmailAndExpiredFalse(String email) {
        long count = entityManager.createQuery(
                "SELECT COUNT(a) FROM _2auth a WHERE a.email = :email AND a.expired = false", Long.class)
                .setParameter("email", email)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public Optional<_2auth> findByEmailAndExpiredFalse(String email) {
        return entityManager.createQuery(
                "SELECT a FROM _2auth a WHERE a.email = :email AND a.expired = false", _2auth.class)
                .setParameter("email", email)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public _2auth findByEmailAndOtpAndRfeNoAndExpiredFalse(String email, String otp, String refNo) {
        return entityManager.createQuery(
                "SELECT a FROM _2auth a WHERE a.email = :email AND a.otp = :otp AND a.refNo = :refNo AND a.expired = false", _2auth.class)
                .setParameter("email", email)
                .setParameter("otp", otp)
                .setParameter("refNo", refNo)
                .getSingleResult();
    }

    @Override
    public _2auth update(_2auth auth) {
        entityManager.merge(auth);
        return auth;
    }
}