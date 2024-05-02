package name.expenses.services.transaction;




import name.expenses.dtos.CategoryRespDto;
import name.expenses.models.Category;
import name.expenses.models.Page;
import name.expenses.services.utils.CurrentCustomerCollections;
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

//    @Mappings(
//
//            {
//                    @Mapping(target = "id", ignore = true),
//                    @Mapping(target = "deleted", ignore = true),
//                    @Mapping(target = "refNo", ignore = true),
//                    @Mapping(target = "createdAt", ignore = true),
//                    @Mapping(target = "updatedAt", ignore = true),
//                    @Mapping(target = "customers", ignore = true),
//
//            }
//
//    )
//    public abstract Category reqDtoToEntity(CategoryReqDto entityReqDto);
    public abstract CategoryRespDto entityToRespDto(Category entity);
    public abstract Set<CategoryRespDto> entityToRespDto(Set<Category> entities);
    public abstract List<CategoryRespDto> entityToRespDto(List<Category> entities);
    public abstract Page<CategoryRespDto> entityToRespDto(Page<Category> entitiesPage);

//    @Mappings(
//
//            {
//                    @Mapping(target = "id", ignore = true),
//                    @Mapping(target = "deleted", ignore = true),
//                    @Mapping(target = "refNo", ignore = true),
//                    @Mapping(target = "createdAt", ignore = true),
//                    @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())"),
//                    @Mapping(target = "subCategories", ignore = true),
//                    @Mapping(target = "customers", ignore = true),
//
//            }
//
//    )
//    public abstract void update(@MappingTarget Category entity, CategoryUpdateDto entityUpdateDto);

}
