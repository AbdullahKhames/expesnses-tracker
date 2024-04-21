package name.expenses.models;


import java.util.Set;

public interface CategoryGetter  extends GetRefNo {
    Set<Category> getCategories();
    void setCategories(Set<Category> categories);
}