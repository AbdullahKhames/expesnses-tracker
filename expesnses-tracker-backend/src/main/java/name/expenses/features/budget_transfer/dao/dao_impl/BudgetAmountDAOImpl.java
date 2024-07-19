package name.expenses.features.budget_transfer.dao.dao_impl;

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
import name.expenses.features.budget_transfer.dao.BudgetAmountDAO;

import name.expenses.features.budget_transfer.models.BudgetAmount;
import name.expenses.globals.Page;
import name.expenses.globals.SortDirection;
import name.expenses.utils.FieldValidator;
import name.expenses.utils.PageUtil;

import java.util.*;

@Stateless
@Interceptors(RepoAdvice.class)
@Transactional
public class BudgetAmountDAOImpl implements BudgetAmountDAO {
    @PersistenceContext(unitName = "expenses-unit")
    private EntityManager entityManager;

    @Override
    public BudgetAmount create(BudgetAmount budgetAmount) {

        try{
            entityManager.getTransaction().begin();
            if (budgetAmount.getId() != null && entityManager.find(BudgetAmount.class, budgetAmount.getId()) != null) {
                BudgetAmount budgetAmount1 = entityManager.merge(budgetAmount);
                entityManager.getTransaction().commit();
                return budgetAmount1;
            } else {
                entityManager.persist(budgetAmount);
                entityManager.getTransaction().commit();
                return budgetAmount;
            }
        }catch (Exception ex){
            entityManager.getTransaction().rollback();
            throw new GeneralFailureException(GeneralFailureException.ERROR_PERSISTING,
                    Map.of("original error message", ex.getMessage(),
                            "error", "there was an error with your request couldn't persist"));
        }
    }

    @Override
    public Optional<BudgetAmount> get(String refNo) {
        try {
            TypedQuery<BudgetAmount> budgetAmountTypedQuery = entityManager.createQuery("SELECT e from BudgetAmount e WHERE e.refNo = :refNo", BudgetAmount.class);
            budgetAmountTypedQuery.setParameter("refNo", refNo);
            return Optional.ofNullable(budgetAmountTypedQuery.getSingleResult());
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
    public Page<BudgetAmount> findAll(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<BudgetAmount> query = cb.createQuery(BudgetAmount.class);
        Root<BudgetAmount> root = query.from(BudgetAmount.class);

        query.select(root);

        Path<Object> sortByPath;
        if (FieldValidator.hasField(sortBy, BudgetAmount.class)) {
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
            TypedQuery<BudgetAmount> typedQuery = entityManager.createQuery(query);
            typedQuery.setFirstResult((int) ((pageNumber - 1) * pageSize));
            typedQuery.setMaxResults(Math.toIntExact(pageSize));
            List<BudgetAmount> budgetAmounts = typedQuery.getResultList();
            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            countQuery.select(cb.count(countQuery.from(BudgetAmount.class)));
            Long totalElements = entityManager.createQuery(countQuery).getSingleResult();
            return PageUtil.createPage(pageNumber, pageSize, budgetAmounts, totalElements);

        }catch (Exception exception){
            throw new GeneralFailureException(GeneralFailureException.ERROR_FETCH,
                    Map.of("original message", exception.getMessage().substring(0, 15),
                            "error", "there was an error with your request"));
        }

    }

    @Override
    public List<BudgetAmount> get() {
        return entityManager.createQuery("SELECT e FROM BudgetAmount e", BudgetAmount.class).getResultList();
    }

    @Override
    public BudgetAmount update(BudgetAmount budgetAmount) {
        try {
            entityManager.getTransaction().begin();
            BudgetAmount updatedbudgetAmount = entityManager.merge(budgetAmount);
            entityManager.getTransaction().commit();
            return updatedbudgetAmount;

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
            TypedQuery<BudgetAmount> budgetAmountTypedQuery = entityManager.createQuery("SELECT e from BudgetAmount e WHERE e.refNo = :refNo", BudgetAmount.class);
            budgetAmountTypedQuery.setParameter("refNo", refNo);
            BudgetAmount budgetAmount = budgetAmountTypedQuery.getSingleResult();
            if (budgetAmount != null) {
                entityManager.remove(budgetAmount);
            }
            return refNo;
        }catch (Exception ex){
            throw new GeneralFailureException(GeneralFailureException.ERROR_DELETE,
                    Map.of("original error message", ex.getMessage().substring(0, 15),
                            "error", "there was an error with your request couldn't delete entity"));
        }
    }

    @Override
    public Set<BudgetAmount> getEntities(Set<String> refNos) {
        if (refNos == null || refNos.isEmpty()) {
            return new HashSet<>();
        }

        TypedQuery<BudgetAmount> query = entityManager.createQuery(
                "SELECT a FROM BudgetAmount a WHERE a.refNo IN :refNos", BudgetAmount.class);
        query.setParameter("refNos", refNos);
        return new HashSet<>(query.getResultList());
    }
}
