package name.expenses.features.category.mappers;



import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import name.expenses.features.association.Models;
import name.expenses.features.category.dtos.request.CategoryReqDto;
import name.expenses.features.category.dtos.request.CategoryUpdateDto;

import name.expenses.features.category.dtos.response.CategoryRespDto;
import name.expenses.features.category.models.Category;
import name.expenses.features.expesnse.models.Expense;
import name.expenses.features.sub_category.dtos.response.SubCategoryRespDto;
import name.expenses.features.sub_category.mappers.SubCategoryMapper;
import name.expenses.features.sub_category.models.SubCategory;
import name.expenses.globals.Page;
import name.expenses.utils.CurrentUserFromContext;
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
public abstract class CategoryMapper {
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
    public abstract Category reqDtoToEntity(CategoryReqDto entityReqDto);
    public abstract CategoryRespDto entityToRespDto(Category entity);
    public abstract Set<CategoryRespDto> entityToRespDto(Set<Category> entities);
    public abstract List<CategoryRespDto> entityToRespDto(List<Category> entities);
    public abstract Page<CategoryRespDto> entityToRespDto(Page<Category> entitiesPage);

    @Mappings(

            {
                    @Mapping(target = "id", ignore = true),
                    @Mapping(target = "deleted", ignore = true),
                    @Mapping(target = "refNo", ignore = true),
                    @Mapping(target = "createdAt", ignore = true),
                    @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())"),
                    @Mapping(target = "subCategories", ignore = true),
                    @Mapping(target = "customers", ignore = true),

            }

    )
    public abstract void update(@MappingTarget Category entity, CategoryUpdateDto entityUpdateDto);
    @AfterMapping
    public CategoryRespDto afterEntityToRespDto(Category entity,
                                                @MappingTarget CategoryRespDto.CategoryRespDtoBuilder categoryRespDtoBuilder) {
        categoryRespDtoBuilder
                .currentCustomerRegistered(
                        CurrentUserFromContext
                                .getCurrentUserFromContext(securityContext, entity, Models.CATEGORY));
        CategoryRespDto categoryRespDto = categoryRespDtoBuilder.build();
        categoryRespDto.setTotalSpent(
                categoryRespDto.getSubCategories()
                        .stream()
                        .map(SubCategoryRespDto::getTotalSpent)
                        .reduce(0.0, Double::sum)
        );
        return categoryRespDto;
    }
}
