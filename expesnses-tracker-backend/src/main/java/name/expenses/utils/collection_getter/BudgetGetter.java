package name.expenses.utils.collection_getter;

import name.expenses.features.budget.models.Budget;
import name.expenses.globals.GetRefNo;

import java.util.Set;

public interface BudgetGetter extends GetRefNo {
    Set<Budget> getBudgets();
    void setBudgets(Set<Budget> budgets);
}