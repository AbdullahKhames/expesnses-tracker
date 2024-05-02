package name.expenses.services.transaction;



import name.expenses.dtos.PocketReqDto;
import name.expenses.dtos.PocketRespDto;
import name.expenses.dtos.PocketUpdateDto;
import name.expenses.models.Customer;
import name.expenses.models.Page;
import name.expenses.models.Pocket;
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
public abstract class PocketMapper {

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
    public abstract Pocket reqDtoToEntity(PocketReqDto entityReqDto);
    @Mappings(

            {
//                    @Mapping(target = "customer", ignore = true),
                    @Mapping(target = "customerName", source = "customer", qualifiedByName = "getCustomerName"),
                    @Mapping(target = "accountName", source = "account.name"),
                    @Mapping(target = "accountRefNo", source = "account.refNo"),
            }

    )
    public abstract PocketRespDto entityToRespDto(Pocket entity);

    @Named("getCustomerName")
    public String getCustomerName(Customer customer){
        return customer.getUser().getFullName();
    }
    public abstract Set<PocketRespDto> entityToRespDto(Set<Pocket> entities);
    public abstract List<PocketRespDto> entityToRespDto(List<Pocket> entities);
    public abstract Page<PocketRespDto> entityToRespDto(Page<Pocket> entitiesPage);

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
    public abstract void update(@MappingTarget Pocket entity, PocketUpdateDto entityUpdateDto);
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
    public abstract Pocket reqEntityToEntity(PocketUpdateDto newPocket);

}
