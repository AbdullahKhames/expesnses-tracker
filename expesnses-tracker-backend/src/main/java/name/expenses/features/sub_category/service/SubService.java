package name.expenses.features.sub_category.service;

import jakarta.ejb.Local;
import name.expenses.features.category.dtos.request.CategoryUpdateDto;
import name.expenses.features.category.models.Category;
import name.expenses.features.sub_category.dtos.request.SubCategoryReqDto;
import name.expenses.features.sub_category.dtos.request.SubCategoryUpdateDto;
import name.expenses.globals.SortDirection;
import name.expenses.globals.association.CollectionAdder;
import name.expenses.globals.association.CollectionRemover;
import name.expenses.globals.responses.ResponseDto;
import name.expenses.globals.association.UpdateAssociation;

@Local
public interface SubService extends CollectionRemover<Category>, CollectionAdder<Category>, UpdateAssociation<Category, CategoryUpdateDto> {
    ResponseDto create(SubCategoryReqDto expense);
    ResponseDto get(String refNo);

    ResponseDto update(String refNo, SubCategoryUpdateDto expense);

    ResponseDto delete(String refNo);

    ResponseDto getAllEntities(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);

}