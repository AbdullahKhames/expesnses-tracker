package name.expenses.globals.association;

import name.expenses.features.account.models.Account;
import name.expenses.features.category.models.Category;

public interface CollectionAdder <T> {
    boolean addAssociation(T entity, String refNo);
}
