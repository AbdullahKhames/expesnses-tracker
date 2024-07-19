package name.expenses.features.budget_transfer.mappers;



import name.expenses.features.budget_transfer.dtos.request.BudgetTransferReqDto;
import name.expenses.features.budget_transfer.dtos.request.BudgetTransferUpdateDto;
import name.expenses.features.budget_transfer.dtos.response.BudgetTransferRespDto;
import name.expenses.features.budget_transfer.models.BudgetTransfer;
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
        },
        imports = {LocalDateTime.class})
public interface BudgetTransferMapper {
    @Mappings(

            {
                    @Mapping(target = "id", ignore = true),
                    @Mapping(target = "deleted", ignore = true),
                    @Mapping(target = "refNo", ignore = true),
//                    @Mapping(target = "createdAt", ignore = true),
                    @Mapping(target = "updatedAt", ignore = true),
                    @Mapping(target = "senderBudgetAmount", ignore = true),
                    @Mapping(target = "receiverBudgetAmounts", ignore = true),
            }

    )
    BudgetTransfer reqDtoToEntity(BudgetTransferReqDto entityReqDto);
    BudgetTransferRespDto entityToRespDto(BudgetTransfer entity);
    Set<BudgetTransferRespDto> entityToRespDto(Set<BudgetTransfer> entities);
    List<BudgetTransferRespDto> entityToRespDto(List<BudgetTransfer> entities);
    Page<BudgetTransferRespDto> entityToRespDto(Page<BudgetTransfer> entitiesPage);

    @Mappings(

            {
                    @Mapping(target = "id", ignore = true),
                    @Mapping(target = "deleted", ignore = true),
                    @Mapping(target = "refNo", ignore = true),
//                    @Mapping(target = "createdAt", ignore = true),
                    @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())"),
                    @Mapping(target = "senderBudgetAmount", ignore = true),
                    @Mapping(target = "receiverBudgetAmounts", ignore = true),
            }

    )
    void update(@MappingTarget BudgetTransfer entity, BudgetTransferUpdateDto entityUpdateDto);
}
