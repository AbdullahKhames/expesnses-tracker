package name.expenses.features.expesnse.dao.dao_impl;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import name.expenses.features.expesnse.dao.ExpenseDAO;
import name.expenses.features.expesnse.models.Expense;

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
    public Expense getExpense(Long id) {
        return entityManager.find(Expense.class, id);
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
    public void deleteExpense(Long id) {
        Expense expense = entityManager.find(Expense.class, id);
        if (expense != null) {
            entityManager.remove(expense);
        }
    }
}