package name.expenses.features.expesnse.mappers;


import jakarta.ejb.Stateless;
import name.expenses.features.expesnse.dtos.request.ExpenseReqDto;
import name.expenses.features.expesnse.dtos.request.ExpenseUpdateDto;
import name.expenses.features.expesnse.dtos.response.ExpenseRespDto;
import name.expenses.features.expesnse.models.Expense;
import name.expenses.globals.Page;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "JAKARTA",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        imports = {LocalDateTime.class})
public interface ExpenseMapper {
    @Mappings(

            {
                    @Mapping(target = "id", ignore = true),
                    @Mapping(target = "deleted", ignore = true),
                    @Mapping(target = "refNo", ignore = true),
                    @Mapping(target = "createdAt", ignore = true),
                    @Mapping(target = "updatedAt", ignore = true),
            }

    )
    Expense reqDtoToEntity(ExpenseReqDto entityReqDto);
    ExpenseRespDto entityToRespDto(Expense entity);
    Set<ExpenseRespDto> entityToRespDto(Set<Expense> entities);
    List<ExpenseRespDto> entityToRespDto(List<Expense> entities);
    Page<ExpenseRespDto> entityToRespDto(Page<Expense> expensePage);

    @Mappings(

            {
                    @Mapping(target = "id", ignore = true),
                    @Mapping(target = "deleted", ignore = true),
                    @Mapping(target = "refNo", ignore = true),
                    @Mapping(target = "createdAt", ignore = true),
                    @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())"),

            }

    )
    void update(@MappingTarget Expense entity, ExpenseUpdateDto entityUpdateDto);
    @Mappings(

            {
                    @Mapping(target = "id", ignore = true),
                    @Mapping(target = "deleted", ignore = true),
                    @Mapping(target = "refNo", ignore = true),
                    @Mapping(target = "createdAt", ignore = true),
                    @Mapping(target = "updatedAt", ignore = true),

            }

    )
    Expense reqEntityToEntity(ExpenseUpdateDto newExpense);
}
