package name.expenses.services.transaction;



import name.expenses.dtos.TransactionReqDto;
import name.expenses.dtos.TransactionRespDto;
import name.expenses.dtos.TransactionUpdateDto;
import name.expenses.models.Page;
import name.expenses.models.Transaction;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "JAKARTA",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {
                PocketAmountMapper.class,
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
