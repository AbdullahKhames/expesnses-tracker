package name.expenses.services.transaction;




import name.expenses.dtos.SubCategoryReqDto;
import name.expenses.dtos.SubCategoryRespDto;
import name.expenses.models.Expense;
import name.expenses.models.Models;
import name.expenses.models.Page;
import name.expenses.models.SubCategory;
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

//    @Mappings(
//
//            {
//                    @Mapping(target = "id", ignore = true),
//                    @Mapping(target = "deleted", ignore = true),
//                    @Mapping(target = "refNo", ignore = true),
//                    @Mapping(target = "createdAt", ignore = true),
//                    @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())"),
//                    @Mapping(target = "expenses", ignore = true),
//                    @Mapping(target = "customers", ignore = true),
//
//            }
//
//    )
//    public  abstract void update(@MappingTarget SubCategory entity, SubCategoryUpdateDto entityUpdateDto);
//    @Mappings(
//            {
////                    @Mapping(target = "refNo", expression = "java(subCategoryUpdateDto.getRefNo() != null ? subCategoryUpdateDto.getRefNo() : UUID.randomUUID().toString())"),
//                    @Mapping(target = "deleted", ignore = true),
//                    @Mapping(target = "id", ignore = true),
//                    @Mapping(target = "refNo", ignore = true),
//                    @Mapping(target = "createdAt", ignore = true),
//                    @Mapping(target = "updatedAt", ignore = true),
//                    @Mapping(target = "customers", ignore = true),
//                    @Mapping(target = "expenses", ignore = true),
//
//            }
//    )
//    public  abstract SubCategory reqEntityToEntity(SubCategoryUpdateDto subCategoryUpdateDto);

    @Named("getTotalSpent")
    public Double getTotalSpent(Set<Expense> expenses){
        if (expenses == null) {
            return 0.0;
        }
        return expenses.stream()
                .map(Expense::getAmount)
                .reduce(0.0, Double::sum);
    }
//    @AfterMapping
//    public void afterMapping(@MappingTarget SubCategoryRespDto.SubCategoryRespDtoBuilder subCategoryRespDtoBuilder, SubCategory subCategory){
//        subCategoryRespDtoBuilder
//                .currentCustomerRegistered(
//                        currentCustomerCollections
//                                .isPresentCollection(subCategory, Models.SUB_CATEGORY));
//    }
}
