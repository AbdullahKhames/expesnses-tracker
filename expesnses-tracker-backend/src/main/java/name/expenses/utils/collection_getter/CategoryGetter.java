package name.expenses.utils.collection_getter;

import name.expenses.features.category.models.Category;
import name.expenses.globals.GetRefNo;

import java.util.Set;

public interface CategoryGetter  extends GetRefNo {
    Set<Category> getCategories();
    void setCategories(Set<Category> categories);
}