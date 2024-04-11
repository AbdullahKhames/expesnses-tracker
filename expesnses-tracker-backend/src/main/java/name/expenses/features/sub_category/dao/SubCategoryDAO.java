package name.expenses.features.sub_category.dao;

import jakarta.ejb.Local;
import name.expenses.features.sub_category.models.SubCategory;
import name.expenses.globals.Page;
import name.expenses.globals.SortDirection;

import java.util.List;
import java.util.Optional;

@Local
public interface SubCategoryDAO {
    SubCategory create(SubCategory expense);
    Optional<SubCategory> get(String refNo);
    Page<SubCategory> findAll(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);
    List<SubCategory> get();
    SubCategory update(SubCategory expense);
    String delete(String refNo);

    boolean checkCategoryAssociation(SubCategory subCategory);
}