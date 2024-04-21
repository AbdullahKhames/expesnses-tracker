package name.expenses.models;


import java.util.Set;

public interface AccountGetter extends GetRefNo {
    Set<Account> getAccounts();
    void setAccounts(Set<Account> accounts);
}