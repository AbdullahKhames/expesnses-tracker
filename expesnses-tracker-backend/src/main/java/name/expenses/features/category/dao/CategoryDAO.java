package name.expenses.features.category.dao;

import jakarta.ejb.Local;
import name.expenses.features.category.models.Category;
import name.expenses.globals.Page;
import name.expenses.globals.SortDirection;

import java.util.List;
import java.util.Optional;

@Local
public interface CategoryDAO {
    Category create(Category expense);
    Optional<Category> get(String refNo);
    Page<Category> findAll(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);
    List<Category> get();
    Category update(Category expense);
    String delete(String refNo);
}