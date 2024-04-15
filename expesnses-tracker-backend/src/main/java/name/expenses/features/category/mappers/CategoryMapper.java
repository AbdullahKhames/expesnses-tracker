package name.expenses.features.category.mappers;



import name.expenses.features.category.dtos.request.CategoryReqDto;
import name.expenses.features.category.dtos.request.CategoryUpdateDto;

import name.expenses.features.category.dtos.response.CategoryRespDto;
import name.expenses.features.category.models.Category;
import name.expenses.features.sub_category.mappers.SubCategoryMapper;
import name.expenses.globals.Page;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "JAKARTA",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        uses = {
            SubCategoryMapper.class
        },
        imports = {LocalDateTime.class})
public interface CategoryMapper {
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
    Category reqDtoToEntity(CategoryReqDto entityReqDto);
    CategoryRespDto entityToRespDto(Category entity);
    Set<CategoryRespDto> entityToRespDto(Set<Category> entities);
    List<CategoryRespDto> entityToRespDto(List<Category> entities);
    Page<CategoryRespDto> entityToRespDto(Page<Category> entitiesPage);

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
    void update(@MappingTarget Category entity, CategoryUpdateDto entityUpdateDto);
}
