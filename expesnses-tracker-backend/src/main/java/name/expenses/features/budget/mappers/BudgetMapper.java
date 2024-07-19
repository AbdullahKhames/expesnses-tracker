package name.expenses.features.budget.mappers;


import jakarta.inject.Inject;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import name.expenses.features.account.mappers.AccountMapper;
import name.expenses.features.association.Models;
import name.expenses.features.budget.dtos.request.BudgetReqDto;

import name.expenses.features.budget.dtos.request.BudgetUpdateDto;
import name.expenses.features.budget.dtos.response.BudgetRespDto;
import name.expenses.features.budget.models.Budget;
import name.expenses.features.customer.models.Customer;
import name.expenses.globals.Page;
import name.expenses.utils.CurrentCustomerCollections;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "JAKARTA",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {
                AccountMapper.class
        },
        imports = {LocalDateTime.class})
public abstract class BudgetMapper {
    @Context
    private SecurityContext securityContext;
    @Inject
    private CurrentCustomerCollections currentCustomerCollections;
    @Mappings(

            {
                    @Mapping(target = "id", ignore = true),
                    @Mapping(target = "deleted", ignore = true),
                    @Mapping(target = "refNo", ignore = true),
                    @Mapping(target = "createdAt", ignore = true),
                    @Mapping(target = "updatedAt", ignore = true),
                    @Mapping(target = "customer", ignore = true),
            }

    )
    public abstract Budget reqDtoToEntity(BudgetReqDto entityReqDto);
    @Mappings(

            {
//                    @Mapping(target = "customer", ignore = true),
                    @Mapping(target = "customerName", source = "customer", qualifiedByName = "getCustomerName"),
                    @Mapping(target = "accountName", source = "account.name"),
                    @Mapping(target = "accountRefNo", source = "account.refNo"),
            }

    )
    public abstract BudgetRespDto entityToRespDto(Budget entity);

    @Named("getCustomerName")
    public String getCustomerName(Customer customer){
        return customer.getUser().getFullName();
    }
    public abstract Set<BudgetRespDto> entityToRespDto(Set<Budget> entities);
    public abstract List<BudgetRespDto> entityToRespDto(List<Budget> entities);
    public abstract Page<BudgetRespDto> entityToRespDto(Page<Budget> entitiesPage);

    @Mappings(

            {
                    @Mapping(target = "id", ignore = true),
                    @Mapping(target = "deleted", ignore = true),
                    @Mapping(target = "refNo", ignore = true),
                    @Mapping(target = "createdAt", ignore = true),
                    @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())"),
                    @Mapping(target = "customer", ignore = true),

            }

    )
    public abstract void update(@MappingTarget Budget entity, BudgetUpdateDto entityUpdateDto);
    @Mappings(

            {
                    @Mapping(target = "id", ignore = true),
                    @Mapping(target = "deleted", ignore = true),
                    @Mapping(target = "refNo", ignore = true),
                    @Mapping(target = "createdAt", ignore = true),
                    @Mapping(target = "updatedAt", ignore = true),
                    @Mapping(target = "customer", ignore = true),
            }

    )
    public abstract Budget reqEntityToEntity(BudgetUpdateDto newBudget);
    @AfterMapping
    public void afterMapping(@MappingTarget BudgetRespDto.BudgetRespDtoBuilder BudgetRespDtoBuilder, Budget budget){
        BudgetRespDtoBuilder
                .currentCustomerRegistered(
                        currentCustomerCollections
                                .isPresentCollection(budget, Models.Budget));
    }
}
