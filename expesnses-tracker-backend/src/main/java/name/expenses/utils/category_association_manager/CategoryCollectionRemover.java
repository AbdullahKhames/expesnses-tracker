package name.expenses.utils.category_association_manager;

import name.expenses.features.category.models.Category;

public interface CategoryCollectionRemover {
    boolean removeAssociation(Category category, String refNo);
}