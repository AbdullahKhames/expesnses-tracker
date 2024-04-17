package name.expenses.features.pocket_transfer.dao.dao_impl;

import jakarta.ejb.Stateless;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import name.expenses.config.advice.RepoAdvice;
import name.expenses.error.exception.GeneralFailureException;

import name.expenses.features.pocket_transfer.dao.PocketTransferDAO;
import name.expenses.features.pocket_transfer.models.PocketTransfer;
import name.expenses.globals.Page;
import name.expenses.globals.SortDirection;
import name.expenses.utils.FieldValidator;
import name.expenses.utils.PageUtil;

import java.util.*;

@Stateless
@Interceptors(RepoAdvice.class)
@Transactional
public class PocketTransferDAOImpl implements PocketTransferDAO {

    @PersistenceContext(unitName = "expenses-unit")
    private EntityManager entityManager;

    @Override
    public PocketTransfer create(PocketTransfer transaction) {

        try{
            entityManager.getTransaction().begin();
            if (transaction.getId() != null && entityManager.find(PocketTransfer.class, transaction.getId()) != null) {
                PocketTransfer transaction1 = entityManager.merge(transaction);
                entityManager.getTransaction().commit();
                return transaction1;
            } else {
                entityManager.persist(transaction);
                entityManager.getTransaction().commit();
                return transaction;
            }
        }catch (Exception ex){
            entityManager.getTransaction().rollback();
            throw new GeneralFailureException(GeneralFailureException.ERROR_PERSISTING,
                    Map.of("original error message", ex.getMessage(),
                            "error", "there was an error with your request couldn't persist"));
        }
    }

    @Override
    public Optional<PocketTransfer> get(String refNo) {
        try {
            TypedQuery<PocketTransfer> transactionTypedQuery = entityManager.createQuery("SELECT e from PocketTransfer e WHERE e.refNo = :refNo", PocketTransfer.class);
            transactionTypedQuery.setParameter("refNo", refNo);
            return Optional.ofNullable(transactionTypedQuery.getSingleResult());
        }catch (NoResultException ex){
            return Optional.empty();
        }catch (NonUniqueResultException ex){
            throw new GeneralFailureException(GeneralFailureException.NON_UNIQUE_IDENTIFIER,
                    Map.of("error", String.format("the query didn't return a single result for reference number %s", refNo)));
        }catch (Exception ex){
            throw new GeneralFailureException(GeneralFailureException.OBJECT_NOT_FOUND,
                    Map.of("original error message", ex.getMessage(),
                            "error", String.format("there was an error with your request couldn't find object with reference number %s", refNo)));
        }
    }

    @Override
    public Page<PocketTransfer> findAll(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<PocketTransfer> query = cb.createQuery(PocketTransfer.class);
        Root<PocketTransfer> root = query.from(PocketTransfer.class);

        query.select(root);

        Path<Object> sortByPath;
        if (FieldValidator.hasField(sortBy, PocketTransfer.class)) {
            sortByPath = root.get(sortBy);
        }else {
            sortByPath = root.get("id");
        }

        if (sortDirection == SortDirection.ASC) {
            query.orderBy(cb.asc(sortByPath));
        } else {
            query.orderBy(cb.desc(sortByPath));
        }
        try {
            TypedQuery<PocketTransfer> typedQuery = entityManager.createQuery(query);
            typedQuery.setFirstResult((int) ((pageNumber - 1) * pageSize));
            typedQuery.setMaxResults(Math.toIntExact(pageSize));
            List<PocketTransfer> transactions = typedQuery.getResultList();
            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            countQuery.select(cb.count(countQuery.from(PocketTransfer.class)));
            Long totalElements = entityManager.createQuery(countQuery).getSingleResult();
            return PageUtil.createPage(pageNumber, pageSize, transactions, totalElements);

        }catch (Exception exception){
            throw new GeneralFailureException(GeneralFailureException.ERROR_FETCH,
                    Map.of("original message", exception.getMessage().substring(0, 15),
                            "error", "there was an error with your request"));
        }

    }

    @Override
    public List<PocketTransfer> get() {
        return entityManager.createQuery("SELECT e FROM PocketTransfer e", PocketTransfer.class).getResultList();
    }

    @Override
    public PocketTransfer update(PocketTransfer transaction) {
        try {
            entityManager.getTransaction().begin();
            PocketTransfer updatedPocketTransfer = entityManager.merge(transaction);
            entityManager.getTransaction().commit();
            return updatedPocketTransfer;

        }catch (Exception ex){
            entityManager.getTransaction().rollback();
            throw new GeneralFailureException(GeneralFailureException.ERROR_UPDATE,
                    Map.of("original error message", ex.getMessage().substring(0, 15),
                            "error", "there was an error with your request couldn't update entity"));
        }
    }

    @Override
    public String delete(String refNo) {
        try {
            TypedQuery<PocketTransfer> transactionTypedQuery = entityManager.createQuery("SELECT e from PocketTransfer e WHERE e.refNo = :refNo", PocketTransfer.class);
            transactionTypedQuery.setParameter("refNo", refNo);
            PocketTransfer transaction = transactionTypedQuery.getSingleResult();
            if (transaction != null) {
                entityManager.remove(transaction);
            }
            return refNo;
        }catch (Exception ex){
            throw new GeneralFailureException(GeneralFailureException.ERROR_DELETE,
                    Map.of("original error message", ex.getMessage().substring(0, 15),
                            "error", "there was an error with your request couldn't delete entity"));
        }
    }

    @Override
    public Set<PocketTransfer> getEntities(Set<String> refNos) {
        if (refNos == null || refNos.isEmpty()) {
            return new HashSet<>();
        }

        TypedQuery<PocketTransfer> query = entityManager.createQuery(
                "SELECT a FROM PocketTransfer a WHERE a.refNo IN :refNos", PocketTransfer.class);
        query.setParameter("refNos", refNos);
        return new HashSet<>(query.getResultList());
    }
}