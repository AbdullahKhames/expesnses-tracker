package name.expenses.features.expesnse.service;

import jakarta.ejb.Local;
import name.expenses.features.association.CollectionAdder;
import name.expenses.features.association.CollectionRemover;
import name.expenses.features.customer.models.Customer;
import name.expenses.features.expesnse.dtos.request.ExpenseReqDto;
import name.expenses.features.expesnse.dtos.request.ExpenseUpdateDto;
import name.expenses.features.expesnse.dtos.response.ExpenseRespDto;
import name.expenses.features.expesnse.models.Expense;
import name.expenses.features.sub_category.dtos.request.SubCategoryUpdateDto;
import name.expenses.features.sub_category.models.SubCategory;
import name.expenses.globals.CrudService;
import name.expenses.globals.SortDirection;
import name.expenses.globals.responses.ResponseDto;

import java.util.Set;


@Local
public interface ExpenseService extends
        CrudService<ExpenseReqDto, ExpenseUpdateDto, String, Expense>,
//        CollectionRemover<Category, SubCategoryUpdateDto>,
//        CollectionAdder<Category, SubCategoryUpdateDto>,
        CollectionRemover<SubCategory>,
        CollectionAdder<SubCategory>{
    Expense save(Expense sentExpense);


    void associateSubCategory(String subCatRefNo, Expense sentExpense, Customer customer);

    void updateExpensesAssociation(SubCategory existingSubCategory, SubCategoryUpdateDto newSubCategory);
    Expense reqDtoToEntity(ExpenseReqDto expenseReqDto);

    ResponseDto getAllEntitiesWithoutSubCategory(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);

    Set<ExpenseRespDto> entityToRespDto(Set<Expense> expenses);

    ResponseDto getExpenseByName(String name);
}
