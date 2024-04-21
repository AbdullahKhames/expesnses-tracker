package name.expenses.models;


import java.util.Set;

public interface SubCategoryGetter extends GetRefNo {
    Set<SubCategory> getSubCategories();
    void setSubCategories(Set<SubCategory> subCategories);
}