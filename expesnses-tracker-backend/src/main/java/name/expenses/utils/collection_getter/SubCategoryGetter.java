package name.expenses.utils.collection_getter;

import name.expenses.features.sub_category.models.SubCategory;
import name.expenses.globals.GetRefNo;

import java.util.Set;

public interface SubCategoryGetter extends GetRefNo {
    Set<SubCategory> getSubCategories();
    void setSubCategories(Set<SubCategory> subCategories);
}