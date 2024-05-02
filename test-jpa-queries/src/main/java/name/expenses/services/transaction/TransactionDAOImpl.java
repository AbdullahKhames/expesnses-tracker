package name.expenses.services.transaction;


import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import name.expenses.exceptions.GeneralFailureException;
import name.expenses.models.Page;
import name.expenses.models.SortDirection;
import name.expenses.models.Transaction;
import name.expenses.services.utils.FieldValidator;
import name.expenses.services.utils.PageUtil;

import java.util.*;


@Transactional
public class TransactionDAOImpl implements TransactionDAO {

    @PersistenceContext(unitName = "expenses-unit")
    private EntityManager entityManager;

    @Override
    public Transaction create(Transaction transaction) {

        try{
            entityManager.getTransaction().begin();
            if (transaction.getId() != null && entityManager.find(Transaction.class, transaction.getId()) != null) {
                Transaction transaction1 = entityManager.merge(transaction);
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
    public Optional<Transaction> get(String refNo) {
        try {
            TypedQuery<Transaction> transactionTypedQuery = entityManager.createQuery("SELECT e from Transaction e WHERE e.refNo = :refNo", Transaction.class);
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
    public Page<Transaction> findAll(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Transaction> query = cb.createQuery(Transaction.class);
        Root<Transaction> root = query.from(Transaction.class);

        query.select(root);

        Path<Object> sortByPath;
        if (FieldValidator.hasField(sortBy, Transaction.class)) {
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
            TypedQuery<Transaction> typedQuery = entityManager.createQuery(query);
            typedQuery.setFirstResult((int) ((pageNumber - 1) * pageSize));
            typedQuery.setMaxResults(Math.toIntExact(pageSize));
            List<Transaction> transactions = typedQuery.getResultList();
            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            countQuery.select(cb.count(countQuery.from(Transaction.class)));
            Long totalElements = entityManager.createQuery(countQuery).getSingleResult();
            return PageUtil.createPage(pageNumber, pageSize, transactions, totalElements);

        }catch (Exception exception){
            throw new GeneralFailureException(GeneralFailureException.ERROR_FETCH,
                    Map.of("original message", exception.getMessage().substring(0, 15),
                            "error", "there was an error with your request"));
        }

    }

    @Override
    public List<Transaction> get() {
        return entityManager.createQuery("SELECT e FROM Transaction e", Transaction.class).getResultList();
    }

    @Override
    public Transaction update(Transaction transaction) {
        try {
            entityManager.getTransaction().begin();
            Transaction updatedTransaction = entityManager.merge(transaction);
            entityManager.getTransaction().commit();
            return updatedTransaction;

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
            TypedQuery<Transaction> transactionTypedQuery = entityManager.createQuery("SELECT e from Transaction e WHERE e.refNo = :refNo", Transaction.class);
            transactionTypedQuery.setParameter("refNo", refNo);
            Transaction transaction = transactionTypedQuery.getSingleResult();
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
    public Set<Transaction> getEntities(Set<String> refNos) {
        if (refNos == null || refNos.isEmpty()) {
            return new HashSet<>();
        }

        TypedQuery<Transaction> query = entityManager.createQuery(
                "SELECT a FROM Transaction a WHERE a.refNo IN :refNos", Transaction.class);
        query.setParameter("refNos", refNos);
        return new HashSet<>(query.getResultList());
    }
}