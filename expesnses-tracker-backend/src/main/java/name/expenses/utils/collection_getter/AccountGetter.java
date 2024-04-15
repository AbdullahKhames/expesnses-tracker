package name.expenses.utils.collection_getter;

import name.expenses.features.account.models.Account;
import name.expenses.globals.GetRefNo;

import java.util.Set;

public interface AccountGetter extends GetRefNo {
    Set<Account> getAccounts();
    void setAccounts(Set<Account> accounts);
}