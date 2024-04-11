package name.expenses.globals.association;

import name.expenses.features.category.dtos.request.CategoryUpdateDto;
import name.expenses.features.category.models.Category;

public interface UpdateAssociation <T, J> {
    void updateAssociation(T entity, J entityUpdateDto);
}