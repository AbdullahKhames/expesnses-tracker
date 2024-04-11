package name.expenses.globals.association;

import name.expenses.features.account.models.Account;

public interface CollectionRemover  <T> {
    boolean removeAssociation(T entity, String refNo);
}