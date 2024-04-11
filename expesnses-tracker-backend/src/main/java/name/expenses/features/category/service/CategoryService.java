package name.expenses.features.category.service;

import jakarta.ejb.Local;

import name.expenses.features.category.dtos.request.CategoryReqDto;
import name.expenses.features.category.dtos.request.CategoryUpdateDto;
import name.expenses.globals.SortDirection;
import name.expenses.globals.responses.ResponseDto;

@Local
public interface CategoryService {
    ResponseDto create(CategoryReqDto expense);
    ResponseDto get(String refNo);

    ResponseDto update(String refNo, CategoryUpdateDto expense);

    ResponseDto delete(String refNo);
    ResponseDto addAssociation(String categoryRefNo, String subCategoryRefNo);

    ResponseDto removeAssociation(String categoryRefNo, String subCategoryRefNo);

    ResponseDto getAllEntities(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);

}