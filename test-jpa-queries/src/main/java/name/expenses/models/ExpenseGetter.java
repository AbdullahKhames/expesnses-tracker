package name.expenses.models;


import java.util.Set;

public interface ExpenseGetter extends GetRefNo {
    Set<Expense> getExpenses();
    void setExpenses(Set<Expense> expenses);

}