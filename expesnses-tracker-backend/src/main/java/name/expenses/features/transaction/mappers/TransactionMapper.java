package name.expenses.features.transaction.mappers;


import name.expenses.features.expesnse.mappers.ExpenseMapper;
import name.expenses.features.budget_transfer.mappers.BudgetAmountMapper;
import name.expenses.features.transaction.dtos.request.TransactionReqDto;
import name.expenses.features.transaction.dtos.request.TransactionUpdateDto;
import name.expenses.features.transaction.dtos.response.TransactionRespDto;
import name.expenses.features.transaction.models.Transaction;
import name.expenses.globals.Page;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "JAKARTA",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {
                BudgetAmountMapper.class,
                ExpenseMapper.class
        },
        imports = {LocalDateTime.class})
public interface TransactionMapper {
    @Mappings(

            {
                    @Mapping(target = "id", ignore = true),
                    @Mapping(target = "deleted", ignore = true),
                    @Mapping(target = "refNo", ignore = true),
                    @Mapping(target = "createdAt", ignore = true),
                    @Mapping(target = "updatedAt", ignore = true),
                    @Mapping(target = "expense", ignore = true)
            }

    )
    Transaction reqDtoToEntity(TransactionReqDto entityReqDto);
    TransactionRespDto entityToRespDto(Transaction entity);
    Set<TransactionRespDto> entityToRespDto(Set<Transaction> entities);
    List<TransactionRespDto> entityToRespDto(List<Transaction> entities);
    Page<TransactionRespDto> entityToRespDto(Page<Transaction> entitiesPage);

    @Mappings(

            {
                    @Mapping(target = "id", ignore = true),
                    @Mapping(target = "deleted", ignore = true),
                    @Mapping(target = "refNo", ignore = true),
                    @Mapping(target = "createdAt", ignore = true),
                    @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())"),
            }

    )
    void update(@MappingTarget Transaction entity, TransactionUpdateDto entityUpdateDto);
}
