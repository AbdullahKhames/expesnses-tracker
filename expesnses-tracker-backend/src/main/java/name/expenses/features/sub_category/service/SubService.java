package name.expenses.features.sub_category.service;

import jakarta.ejb.Local;
import name.expenses.features.category.dtos.request.CategoryUpdateDto;
import name.expenses.features.category.models.Category;
import name.expenses.features.sub_category.dtos.request.SubCategoryReqDto;
import name.expenses.features.sub_category.dtos.request.SubCategoryUpdateDto;
import name.expenses.features.sub_category.models.SubCategory;
import name.expenses.globals.CrudService;
import name.expenses.features.association.CollectionAdder;
import name.expenses.features.association.CollectionRemover;
import name.expenses.features.association.UpdateAssociation;
import name.expenses.globals.SortDirection;
import name.expenses.globals.responses.ResponseDto;

@Local
public interface SubService extends
//        CollectionRemover<Category, SubCategoryUpdateDto>,
//        CollectionAdder<Category, SubCategoryUpdateDto>,
        CollectionRemover<Category>,
        CollectionAdder<Category>,
        UpdateAssociation<Category, CategoryUpdateDto>,
        CrudService<SubCategoryReqDto, SubCategoryUpdateDto, String, SubCategory> {

    ResponseDto getAllEntitiesWithoutCategory(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);

    ResponseDto getSubCategoryExpenses(String refNo);

    ResponseDto getSubCategoryByName(String name);
}