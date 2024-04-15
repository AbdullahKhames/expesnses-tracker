package name.expenses.utils.collection_getter;

import name.expenses.features.category.models.Category;
import name.expenses.features.expesnse.models.Expense;
import name.expenses.globals.GetRefNo;

import java.util.Set;

public interface ExpenseGetter extends GetRefNo {
    Set<Expense> getExpenses();
    void setExpenses(Set<Expense> expenses);

}