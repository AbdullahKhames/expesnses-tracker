package name.expenses.services.transaction;



import name.expenses.dtos.ExpenseReqDto;
import name.expenses.dtos.ExpenseRespDto;
import name.expenses.dtos.ExpenseUpdateDto;
import name.expenses.models.Expense;
import name.expenses.models.Page;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "JAKARTA",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {
                SubCategoryMapper.class
        },
        imports = {LocalDateTime.class})
public abstract class ExpenseMapper {

    @Mappings(

            {
                    @Mapping(target = "id", ignore = true),
                    @Mapping(target = "deleted", ignore = true),
                    @Mapping(target = "refNo", ignore = true),
//                    @Mapping(target = "createdAt", ignore = true),
                    @Mapping(target = "updatedAt", ignore = true),
                    @Mapping(target = "customer", ignore = true),

            }

    )
    public abstract Expense reqDtoToEntity(ExpenseReqDto entityReqDto);
    @Mappings(

            {
                    @Mapping(target = "customer", ignore = true),
                    @Mapping(target = "subCategoryRefNo", source = "subCategory.refNo")
            }

    )
    public abstract ExpenseRespDto entityToRespDto(Expense entity);
    public abstract Set<ExpenseRespDto> entityToRespDto(Set<Expense> entities);
    public abstract List<ExpenseRespDto> entityToRespDto(List<Expense> entities);
    public abstract Page<ExpenseRespDto> entityToRespDto(Page<Expense> expensePage);

    @Mappings(

            {
                    @Mapping(target = "id", ignore = true),
                    @Mapping(target = "deleted", ignore = true),
                    @Mapping(target = "refNo", ignore = true),
//                    @Mapping(target = "createdAt", ignore = true),
                    @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())"),
                    @Mapping(target = "customer", ignore = true),

            }

    )
    public abstract void update(@MappingTarget Expense entity, ExpenseUpdateDto entityUpdateDto);
    @Mappings(

            {
                    @Mapping(target = "id", ignore = true),
                    @Mapping(target = "deleted", ignore = true),
                    @Mapping(target = "refNo", ignore = true),
//                    @Mapping(target = "createdAt", ignore = true),
                    @Mapping(target = "updatedAt", ignore = true),
                    @Mapping(target = "customer", ignore = true),


            }

    )
    public abstract Expense reqEntityToEntity(ExpenseUpdateDto newExpense);
//    @AfterMapping
//    public void afterMapping(@MappingTarget ExpenseRespDto.ExpenseRespDtoBuilder expenseRespDtoBuilder, Expense expense){
//        expenseRespDtoBuilder
//                .currentCustomerRegistered(
//                        currentCustomerCollections
//                                .isPresentCollection(expense, Models.EXPENSE));
//    }
}
