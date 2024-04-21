package name.expenses.features.expesnse.dao.dao_impl;

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
import name.expenses.features.category.models.Category;
import name.expenses.features.expesnse.dao.ExpenseDAO;
import name.expenses.features.expesnse.models.Expense;
import name.expenses.globals.Page;
import name.expenses.globals.SortDirection;
import name.expenses.utils.FieldValidator;
import name.expenses.utils.PageUtil;

import java.util.*;
import java.util.stream.Collectors;

@Stateless
@Interceptors(RepoAdvice.class)
@Transactional
public class ExpenseDAOImpl implements ExpenseDAO {

    @PersistenceContext(unitName = "expenses-unit")
    private EntityManager entityManager;

    @Override
    public Expense createExpense(Expense expense) {
        try{
            if (expense.getId() != null && entityManager.find(Expense.class, expense.getId()) != null) {
                return entityManager.merge(expense);
            } else {
                entityManager.persist(expense);
                return expense;
            }
        }catch (Exception ex){
            throw new GeneralFailureException(GeneralFailureException.ERROR_PERSISTING,
                    Map.of("original error message", ex.getMessage(),
                            "error", "there was an error with your request couldn't persist"));
        }

    }

    @Override
    public Optional<Expense> getExpense(String refNo) {

        try {
            TypedQuery<Expense> expenseTypedQuery = entityManager.createQuery("SELECT e from Expense e WHERE e.refNo = :refNo", Expense.class);
            expenseTypedQuery.setParameter("refNo", refNo);
            return Optional.ofNullable(expenseTypedQuery.getSingleResult());
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
    public Page<Expense> findAll(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Expense> query = cb.createQuery(Expense.class);
        Root<Expense> root = query.from(Expense.class);

        query.select(root);

        Path<Object> sortByPath;
        if (FieldValidator.hasField(sortBy, Expense.class)) {
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
            TypedQuery<Expense> typedQuery = entityManager.createQuery(query);
            typedQuery.setFirstResult((int) ((pageNumber - 1) * pageSize));
            typedQuery.setMaxResults(Math.toIntExact(pageSize));
            List<Expense> expenses = typedQuery.getResultList();
            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            countQuery.select(cb.count(countQuery.from(Expense.class)));
            Long totalElements = entityManager.createQuery(countQuery).getSingleResult();
            return PageUtil.createPage(pageNumber, pageSize, expenses, totalElements);
        }catch (Exception exception){
            throw new GeneralFailureException(GeneralFailureException.ERROR_FETCH,
                    Map.of("original message", exception.getMessage().substring(0, 15),
                            "error", "there was an error with your request"));
        }

    }

    @Override
    public List<Expense> getAllExpenses() {
        return entityManager.createQuery("SELECT e FROM Expense e", Expense.class).getResultList();
    }

    @Override
    public Expense updateExpense(Expense expense) {
        try {
            return entityManager.merge(expense);
        }catch (Exception ex){
            throw new GeneralFailureException(GeneralFailureException.ERROR_UPDATE,
                    Map.of("original error message", ex.getMessage().substring(0, 15),
                            "error", "there was an error with your request couldn't update entity"));
        }
    }

    @Override
    public String deleteExpense(String refNo) {
        try {
            TypedQuery<Expense> expenseTypedQuery = entityManager.createQuery("SELECT e from Expense e WHERE e.refNo = :refNo", Expense.class);
            expenseTypedQuery.setParameter("refNo", refNo);
            Expense expense = expenseTypedQuery.getSingleResult();
            if (expense != null) {
                entityManager.remove(expense);
            }
            return refNo;
        }catch (Exception ex){
            throw new GeneralFailureException(GeneralFailureException.ERROR_DELETE,
                    Map.of("original error message", ex.getMessage().substring(0, 15),
                            "error", "there was an error with your request couldn't delete entity"));
        }
    }

    @Override
    public Set<Expense> getEntities(Set<String> refNos) {
        if (refNos == null || refNos.isEmpty()) {
            return new HashSet<>();
        }

        TypedQuery<Expense> query = entityManager.createQuery(
                "SELECT a FROM Expense a WHERE a.refNo IN :refNos", Expense.class);
        query.setParameter("refNos", refNos);
        return new HashSet<>(query.getResultList());
    }

    @Override
    public Set<Expense> saveAll(Set<Expense> expenses) {
        Set<Expense> savedExpenses = new HashSet<>();
        if (expenses != null && !expenses.isEmpty()) {
            for (Expense expense : expenses) {
                if (expense != null && expense.getId() != null){
                    entityManager.persist(expense);
                    savedExpenses.add(expense);
                }
            }
//            entityManager.flush();
//            entityManager.clear();
        }
        return savedExpenses;
    }

    @Override
    public Set<Expense> updateAll(Set<Expense> expenses) {
        return expenses
                .stream()
                .map(this::update)
                .collect(Collectors.toSet());
    }

    @Override
    public Expense update(Expense expense) {
        try {
            entityManager.getTransaction().begin();
            Expense updatedExpense = entityManager.merge(expense);
            entityManager.getTransaction().commit();
            return updatedExpense;

        }catch (Exception ex){
            entityManager.getTransaction().rollback();
            throw new GeneralFailureException(GeneralFailureException.ERROR_UPDATE,
                    Map.of("original error message", ex.getMessage().substring(0, 15),
                            "error", "there was an error with your request couldn't update entity"));
        }
    }

    @Override
    public Page<Expense> findAllWithoutSubCategory(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection) {
        try {
            TypedQuery<Expense> typedQuery = entityManager.createQuery("SELECT e FROM Expense e WHERE e.subCategory is null", Expense.class);
            List<Expense> expenses = typedQuery.getResultList();
            return PageUtil.createPage(pageNumber, pageSize, expenses, expenses.size());

        }catch (Exception ex){
            entityManager.getTransaction().rollback();
            throw new GeneralFailureException(GeneralFailureException.ERROR_UPDATE,
                    Map.of("original error message", ex.getMessage().substring(0, 15),
                            "error", "there was an error with your request couldn't update entity"));
        }
    }

    @Override
    public List<Expense> getByName(String name) {
        try {
            TypedQuery<Expense> categoryTypedQuery = entityManager.createQuery("SELECT e from Expense e WHERE e.name like :name", Expense.class);
            categoryTypedQuery.setParameter("name", "%" + name + "%");
            return categoryTypedQuery.getResultList();
        }catch (NoResultException ex){
            return Collections.emptyList();
        }catch (NonUniqueResultException ex){
            throw new GeneralFailureException(GeneralFailureException.NON_UNIQUE_IDENTIFIER,
                    Map.of("error", String.format("the query didn't return a single result for reference number %s", name)));
        }catch (Exception ex){
            throw new GeneralFailureException(GeneralFailureException.OBJECT_NOT_FOUND,
                    Map.of("original error message", ex.getMessage(),
                            "error", String.format("there was an error with your request couldn't find object with reference number %s", name)));
        }
    }
}