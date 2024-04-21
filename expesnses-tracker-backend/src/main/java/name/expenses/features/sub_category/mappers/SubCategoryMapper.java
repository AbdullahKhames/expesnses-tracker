package name.expenses.features.sub_category.mappers;



import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import name.expenses.features.association.Models;
import name.expenses.features.category.mappers.CategoryMapper;
import name.expenses.features.customer.mappers.CustomerMapper;
import name.expenses.features.expesnse.mappers.ExpenseMapper;
import name.expenses.features.expesnse.models.Expense;
import name.expenses.features.pocket.dtos.response.PocketRespDto;
import name.expenses.features.pocket.models.Pocket;
import name.expenses.features.sub_category.dtos.request.SubCategoryReqDto;
import name.expenses.features.sub_category.dtos.request.SubCategoryUpdateDto;
import name.expenses.features.sub_category.dtos.response.SubCategoryRespDto;
import name.expenses.features.sub_category.models.SubCategory;
import name.expenses.globals.Page;
import name.expenses.utils.CurrentUserFromContext;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Mapper(componentModel = "JAKARTA",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {
            ExpenseMapper.class,
                CategoryMapper.class

        }
        ,imports = {
            UUID.class,
            LocalDateTime.class
})
public abstract class SubCategoryMapper {
    @Context
    private SecurityContext securityContext;
    @Mappings(

            {
                    @Mapping(target = "id", ignore = true),
                    @Mapping(target = "deleted", ignore = true),
                    @Mapping(target = "refNo", ignore = true),
                    @Mapping(target = "createdAt", ignore = true),
                    @Mapping(target = "updatedAt", ignore = true),
                    @Mapping(target = "customers", ignore = true),

            }

    )
    public  abstract SubCategory reqDtoToEntity(SubCategoryReqDto entityReqDto);
    @Mappings({
            @Mapping(target = "totalSpent", source = "expenses", qualifiedByName = "getTotalSpent")
    })
    public  abstract SubCategoryRespDto entityToRespDto(SubCategory entity);
    public  abstract Set<SubCategoryRespDto> entityToRespDto(Set<SubCategory> entities);
    public  abstract List<SubCategoryRespDto> entityToRespDto(List<SubCategory> entities);
    public  abstract Page<SubCategoryRespDto> entityToRespDto(Page<SubCategory> entitiesPage);

    @Mappings(

            {
                    @Mapping(target = "id", ignore = true),
                    @Mapping(target = "deleted", ignore = true),
                    @Mapping(target = "refNo", ignore = true),
                    @Mapping(target = "createdAt", ignore = true),
                    @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())"),
                    @Mapping(target = "expenses", ignore = true),
                    @Mapping(target = "customers", ignore = true),

            }

    )
    public  abstract void update(@MappingTarget SubCategory entity, SubCategoryUpdateDto entityUpdateDto);
    @Mappings(
            {
//                    @Mapping(target = "refNo", expression = "java(subCategoryUpdateDto.getRefNo() != null ? subCategoryUpdateDto.getRefNo() : UUID.randomUUID().toString())"),
                    @Mapping(target = "deleted", ignore = true),
                    @Mapping(target = "id", ignore = true),
                    @Mapping(target = "refNo", ignore = true),
                    @Mapping(target = "createdAt", ignore = true),
                    @Mapping(target = "updatedAt", ignore = true),
                    @Mapping(target = "customers", ignore = true),
                    @Mapping(target = "expenses", ignore = true),

            }
    )
    public  abstract SubCategory reqEntityToEntity(SubCategoryUpdateDto subCategoryUpdateDto);

    @Named("getTotalSpent")
    public Double getTotalSpent(Set<Expense> expenses){
        if (expenses == null) {
            return 0.0;
        }
        return expenses.stream()
                .map(Expense::getAmount)
                .reduce(0.0, Double::sum);
    }
    @AfterMapping
    public void afterMapping(@MappingTarget SubCategoryRespDto.SubCategoryRespDtoBuilder subCategoryRespDtoBuilder, SubCategory subCategory){
        subCategoryRespDtoBuilder
                .currentCustomerRegistered(
                        CurrentUserFromContext
                                .getCurrentUserFromContext(securityContext, subCategory, Models.SUB_CATEGORY));
    }
}
