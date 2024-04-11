package name.expenses.features.sub_category.mappers;



import name.expenses.features.expesnse.mappers.ExpenseMapper;
import name.expenses.features.sub_category.dtos.request.SubCategoryReqDto;
import name.expenses.features.sub_category.dtos.request.SubCategoryUpdateDto;
import name.expenses.features.sub_category.dtos.response.SubCategoryRespDto;
import name.expenses.features.sub_category.models.SubCategory;
import name.expenses.globals.Page;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Mapper(componentModel = "JAKARTA",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        uses = {
            ExpenseMapper.class
        }
        ,imports = {
            UUID.class,
            LocalDateTime.class
})
public interface SubCategoryMapper {
    @Mappings(

            {
                    @Mapping(target = "id", ignore = true),
                    @Mapping(target = "deleted", ignore = true),
                    @Mapping(target = "refNo", ignore = true),
                    @Mapping(target = "createdAt", ignore = true),
                    @Mapping(target = "updatedAt", ignore = true),
            }

    )
    SubCategory reqDtoToEntity(SubCategoryReqDto entityReqDto);
    SubCategoryRespDto entityToRespDto(SubCategory entity);
    Set<SubCategoryRespDto> entityToRespDto(Set<SubCategory> entities);
    List<SubCategoryRespDto> entityToRespDto(List<SubCategory> entities);
    Page<SubCategoryRespDto> entityToRespDto(Page<SubCategory> entitiesPage);

    @Mappings(

            {
                    @Mapping(target = "id", ignore = true),
                    @Mapping(target = "deleted", ignore = true),
                    @Mapping(target = "refNo", ignore = true),
                    @Mapping(target = "createdAt", ignore = true),
                    @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())"),
                    @Mapping(target = "expenses", ignore = true),
            }

    )
    void update(@MappingTarget SubCategory entity, SubCategoryUpdateDto entityUpdateDto);
    @Mappings(
            {
//                    @Mapping(target = "refNo", expression = "java(subCategoryUpdateDto.getRefNo() != null ? subCategoryUpdateDto.getRefNo() : UUID.randomUUID().toString())"),
                    @Mapping(target = "deleted", ignore = true),
                    @Mapping(target = "id", ignore = true),
                    @Mapping(target = "refNo", ignore = true),
                    @Mapping(target = "createdAt", ignore = true),
                    @Mapping(target = "updatedAt", ignore = true),
            }
    )
    SubCategory reqEntityToEntity(SubCategoryUpdateDto subCategoryUpdateDto);
}
