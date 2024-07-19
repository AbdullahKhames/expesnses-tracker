package name.expenses.features.budget_transfer.mappers;


import name.expenses.features.budget.mappers.BudgetMapper;
import name.expenses.features.budget.mappers.BudgetMapper;

import name.expenses.features.budget_transfer.dtos.request.BudgetAmountReqDto;
import name.expenses.features.budget_transfer.dtos.request.BudgetAmountUpdateDto;
import name.expenses.features.budget_transfer.dtos.response.BudgetAmountRespDto;
import name.expenses.features.budget_transfer.models.BudgetAmount;
import name.expenses.globals.Page;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "JAKARTA",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        uses = {
                BudgetMapper.class,
        },
        imports = {LocalDateTime.class})
public interface BudgetAmountMapper {
    @Mappings(

            {
                    @Mapping(target = "id", ignore = true),
                    @Mapping(target = "deleted", ignore = true),
                    @Mapping(target = "refNo", ignore = true),
                    @Mapping(target = "createdAt", ignore = true),
                    @Mapping(target = "updatedAt", ignore = true),
                    @Mapping(target = "budget", ignore = true),
                    @Mapping(target = "transaction", ignore = true),
            }

    )
    BudgetAmount reqDtoToEntity(BudgetAmountReqDto entityReqDto);
    BudgetAmountRespDto entityToRespDto(BudgetAmount entity);
    Set<BudgetAmountRespDto> entityToRespDto(Set<BudgetAmount> entities);
    List<BudgetAmountRespDto> entityToRespDto(List<BudgetAmount> entities);
    Page<BudgetAmountRespDto> entityToRespDto(Page<BudgetAmount> entitiesPage);
    @Mappings(

            {
                    @Mapping(target = "id", ignore = true),
                    @Mapping(target = "deleted", ignore = true),
                    @Mapping(target = "refNo", ignore = true),
                    @Mapping(target = "createdAt", ignore = true),
                    @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())"),
                    @Mapping(target = "budget", ignore = true),
                    @Mapping(target = "transaction", ignore = true),
            }

    )
    void update(@MappingTarget BudgetAmount entity, BudgetAmountUpdateDto entityUpdateDto);
    @Mappings(

            {
                    @Mapping(target = "id", ignore = true),
                    @Mapping(target = "deleted", ignore = true),
                    @Mapping(target = "refNo", ignore = true),
                    @Mapping(target = "createdAt", ignore = true),
                    @Mapping(target = "updatedAt", ignore = true),
                    @Mapping(target = "budget", ignore = true),
                    @Mapping(target = "transaction", ignore = true),
            }

    )
    BudgetAmount updateDtoToEntity(BudgetAmountUpdateDto updateDto);
}
