package name.expenses.repostitories;


import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.Data;
import name.expenses.exceptions.GeneralFailureException;
import name.expenses.models._2auth;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Transactional
@Data
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

    @Override
    public _2auth findByEmailAndOtpAndExpiredFalse(String email, String otp) {
//        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
//        CriteriaQuery<_2auth> criteriaQuery = criteriaBuilder.createQuery(_2auth.class);
//        Root<_2auth> root = criteriaQuery.from(_2auth.class);
//
//        Predicate[] predicates = new Predicate[2];
//        predicates[0] = criteriaBuilder.equal(root.get("email"), email);
//        predicates[1] = criteriaBuilder.equal(root.get("otp"), otp);
//
//        criteriaQuery.select(root).where(predicates);
//
//        TypedQuery<_2auth> typedQuery = entityManager.createQuery(criteriaQuery);
//        try {
//            return typedQuery.getSingleResult();
//        }catch (Exception ex){
//            throw new GeneralFailureException("there was an error with your request",
//                    Map.of("error", ex.getMessage()));
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("email", email);
        objectMap.put("otp", otp);
        return findByPredicates(objectMap);
    }

    @Override
    public _2auth findByEmailAndTokenAndExpiredFalse(String email, String token) {
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("email", email);
        objectMap.put("token", token);
        return findByPredicates(objectMap);
    }

    //    private TypedQuery<_2auth> findByPredicates(String email,
//                                                String otp,
//                                                String refNo,
//                                                boolean expired,
//                                                Type type,
//                                                String token,
//                                                String deviceId,
//                                                Date createdAt){
//
//
//    }
    private _2auth findByPredicates(Map<String, Object> objectMap){
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<_2auth> criteriaQuery = criteriaBuilder.createQuery(_2auth.class);
        Root<_2auth> root = criteriaQuery.from(_2auth.class);

        int size = objectMap.size();
        Predicate[] predicates = new Predicate[size];
        int i = 0;
        for (Map.Entry<String, Object> entry: objectMap.entrySet()){
            // need to handle operator and handle nested objects !
            predicates[i] = criteriaBuilder.equal(root.get(entry.getKey()), entry.getValue());
            i++;
        }
        criteriaQuery.select(root).where(predicates);

        TypedQuery<_2auth> typedQuery = entityManager.createQuery(criteriaQuery);
        try {
            return typedQuery.getSingleResult();
        }catch (Exception ex){
            throw new GeneralFailureException("there was an error with your request",
                    Map.of("error", ex.getMessage()));
        }
    }
}