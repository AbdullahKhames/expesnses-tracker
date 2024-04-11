package name.expenses.utils.category_association_manager;

import name.expenses.features.category.dtos.request.CategoryUpdateDto;
import name.expenses.features.category.models.Category;

public interface UpdateCategoryAssociation {
    void updateAssociation(Category category, CategoryUpdateDto categoryUpdateDto);
}