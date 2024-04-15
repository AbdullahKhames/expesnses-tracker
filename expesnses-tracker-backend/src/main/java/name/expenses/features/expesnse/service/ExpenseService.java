package name.expenses.features.expesnse.service;

import jakarta.ejb.Local;
import name.expenses.features.association.CollectionAdder;
import name.expenses.features.association.CollectionRemover;
import name.expenses.features.expesnse.dtos.request.ExpenseReqDto;
import name.expenses.features.expesnse.dtos.request.ExpenseUpdateDto;
import name.expenses.features.expesnse.models.Expense;
import name.expenses.features.sub_category.dtos.request.SubCategoryUpdateDto;
import name.expenses.features.sub_category.models.SubCategory;
import name.expenses.globals.CrudService;


@Local
public interface ExpenseService extends
        CrudService<ExpenseReqDto, ExpenseUpdateDto, String, Expense>,
//        CollectionRemover<Category, SubCategoryUpdateDto>,
//        CollectionAdder<Category, SubCategoryUpdateDto>,
        CollectionRemover<SubCategory>,
        CollectionAdder<SubCategory>{
    void updateExpensesAssociation(SubCategory existingSubCategory, SubCategoryUpdateDto newSubCategory);
}
