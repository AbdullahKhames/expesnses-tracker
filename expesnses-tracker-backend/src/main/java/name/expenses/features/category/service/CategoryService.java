package name.expenses.features.category.service;

import jakarta.ejb.Local;
import name.expenses.features.association.CollectionAdder;
import name.expenses.features.association.CollectionRemover;
import name.expenses.features.category.dtos.request.CategoryReqDto;
import name.expenses.features.category.dtos.request.CategoryUpdateDto;
import name.expenses.features.category.models.Category;
import name.expenses.features.customer.models.Customer;
import name.expenses.globals.CrudService;
import name.expenses.globals.responses.ResponseDto;

@Local
public interface CategoryService extends CrudService<CategoryReqDto, CategoryUpdateDto, String, Category>,
//        CollectionRemover<Customer, CategoryUpdateDto>,
//        CollectionAdder<Customer, CategoryUpdateDto>,
        CollectionRemover<Customer>,
        CollectionAdder<Customer> {
    ResponseDto addAssociation(String categoryRefNo, String subCategoryRefNo);

    ResponseDto removeAssociation(String categoryRefNo, String subCategoryRefNo);

}