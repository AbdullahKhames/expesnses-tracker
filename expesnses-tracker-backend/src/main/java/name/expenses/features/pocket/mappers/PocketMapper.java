package name.expenses.features.pocket.mappers;


import jakarta.inject.Inject;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import name.expenses.features.account.mappers.AccountMapper;
import name.expenses.features.association.Models;
import name.expenses.features.customer.models.Customer;
import name.expenses.features.pocket.dtos.request.PocketReqDto;
import name.expenses.features.pocket.dtos.request.PocketUpdateDto;
import name.expenses.features.pocket.dtos.response.PocketRespDto;
import name.expenses.features.pocket.models.Pocket;
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
public abstract class PocketMapper {
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
    public abstract Pocket reqDtoToEntity(PocketReqDto entityReqDto);
    @Mappings(

            {
//                    @Mapping(target = "customer", ignore = true),
                    @Mapping(target = "customerName", source = "customer", qualifiedByName = "getCustomerName"),
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
    @AfterMapping
    public void afterMapping(@MappingTarget PocketRespDto.PocketRespDtoBuilder pocketRespDtoBuilder, Pocket pocket){
        pocketRespDtoBuilder
                .currentCustomerRegistered(
                        currentCustomerCollections
                                .isPresentCollection(pocket, Models.POCKET));
    }
}
