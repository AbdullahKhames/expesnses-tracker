package name.expenses.features.expesnse.dao.dao_impl;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import name.expenses.features.expesnse.dao.ExpenseDAO;
import name.expenses.features.expesnse.models.Expense;
import name.expenses.globals.Page;
import name.expenses.globals.SortDirection;
import name.expenses.utils.FieldValidator;
import name.expenses.utils.PageUtil;

import java.util.List;

@Stateless
public class ExpenseDAOImpl implements ExpenseDAO {

    @PersistenceContext(unitName = "expenses-unit")
    private EntityManager entityManager;

    @Override
    public Expense createExpense(Expense expense) {
        entityManager.persist(expense);
        return expense;
    }

    @Override
    public Expense getExpense(String refNo) {
        TypedQuery<Expense> expenseTypedQuery = entityManager.createQuery("SELECT e from Expense e WHERE e.refNo = :refNo", Expense.class);
        expenseTypedQuery.setParameter("refNo", refNo);
        return expenseTypedQuery.getSingleResult();
    }

    @Override
    public Page<Expense> findAll(int pageNumber, int pageSize, String sortBy, SortDirection sortDirection) {
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

        TypedQuery<Expense> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((pageNumber - 1) * pageSize);
        typedQuery.setMaxResults(pageSize);
        List<Expense> expenses = typedQuery.getResultList();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        countQuery.select(cb.count(countQuery.from(Expense.class)));
        Long totalElements = entityManager.createQuery(countQuery).getSingleResult();
        return PageUtil.createPage(pageNumber, pageSize, expenses, totalElements);
    }

    @Override
    public List<Expense> getAllExpenses() {
        return entityManager.createQuery("SELECT e FROM Expense e", Expense.class).getResultList();
    }

    @Override
    public Expense updateExpense(Expense expense) {
        return entityManager.merge(expense);
    }

    @Override
    public String deleteExpense(String refNo) {
        TypedQuery<Expense> expenseTypedQuery = entityManager.createQuery("SELECT e from Expense e WHERE e.refNo = :refNo", Expense.class);
        expenseTypedQuery.setParameter("refNo", refNo);
        Expense expense = expenseTypedQuery.getSingleResult();
        if (expense != null) {
            entityManager.remove(expense);
        }
        return refNo;
    }
}