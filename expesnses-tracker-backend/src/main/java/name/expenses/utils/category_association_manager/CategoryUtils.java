package name.expenses.utils.category_association_manager;

import name.expenses.features.category.models.Category;

public class CategoryUtils {
    public static boolean isValidInput(Category category, Object association){
        return category != null && association != null;
    }
}