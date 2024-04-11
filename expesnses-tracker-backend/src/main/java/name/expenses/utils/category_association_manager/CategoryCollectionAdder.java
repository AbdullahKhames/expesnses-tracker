package name.expenses.utils.category_association_manager;

import name.expenses.features.category.models.Category;

public interface CategoryCollectionAdder {
    boolean addAssociation(Category category, String refNo);
}
