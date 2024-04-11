package name.expenses.features.sub_category.service;

import jakarta.ejb.Local;
import name.expenses.features.sub_category.dtos.request.SubCategoryReqDto;
import name.expenses.features.sub_category.dtos.request.SubCategoryUpdateDto;
import name.expenses.globals.SortDirection;
import name.expenses.globals.responses.ResponseDto;
import name.expenses.utils.category_association_manager.CategoryCollectionAdder;
import name.expenses.utils.category_association_manager.CategoryCollectionRemover;
import name.expenses.utils.category_association_manager.UpdateCategoryAssociation;

@Local
public interface SubCategoryService extends CategoryCollectionRemover, CategoryCollectionAdder, UpdateCategoryAssociation {
    ResponseDto create(SubCategoryReqDto expense);
    ResponseDto get(String refNo);

    ResponseDto update(String refNo, SubCategoryUpdateDto expense);

    ResponseDto delete(String refNo);

    ResponseDto getAllEntities(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);

}