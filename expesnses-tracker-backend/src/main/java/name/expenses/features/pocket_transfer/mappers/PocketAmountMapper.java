package name.expenses.features.pocket_transfer.mappers;


import name.expenses.features.pocket.mappers.PocketMapper;

import name.expenses.features.pocket_transfer.dtos.request.PocketAmountReqDto;
import name.expenses.features.pocket_transfer.dtos.request.PocketAmountUpdateDto;
import name.expenses.features.pocket_transfer.dtos.response.PocketAmountRespDto;
import name.expenses.features.pocket_transfer.models.PocketAmount;
import name.expenses.globals.Page;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "JAKARTA",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        uses = {
                PocketMapper.class,
        },
        imports = {LocalDateTime.class})
public interface PocketAmountMapper {
    @Mappings(

            {
                    @Mapping(target = "id", ignore = true),
                    @Mapping(target = "deleted", ignore = true),
                    @Mapping(target = "refNo", ignore = true),
                    @Mapping(target = "createdAt", ignore = true),
                    @Mapping(target = "updatedAt", ignore = true),
                    @Mapping(target = "pocket", ignore = true),
                    @Mapping(target = "transaction", ignore = true),
            }

    )
    PocketAmount reqDtoToEntity(PocketAmountReqDto entityReqDto);
    PocketAmountRespDto entityToRespDto(PocketAmount entity);
    Set<PocketAmountRespDto> entityToRespDto(Set<PocketAmount> entities);
    List<PocketAmountRespDto> entityToRespDto(List<PocketAmount> entities);
    Page<PocketAmountRespDto> entityToRespDto(Page<PocketAmount> entitiesPage);
    @Mappings(

            {
                    @Mapping(target = "id", ignore = true),
                    @Mapping(target = "deleted", ignore = true),
                    @Mapping(target = "refNo", ignore = true),
                    @Mapping(target = "createdAt", ignore = true),
                    @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())"),
                    @Mapping(target = "pocket", ignore = true),
                    @Mapping(target = "transaction", ignore = true),
            }

    )
    void update(@MappingTarget PocketAmount entity, PocketAmountUpdateDto entityUpdateDto);
    @Mappings(

            {
                    @Mapping(target = "id", ignore = true),
                    @Mapping(target = "deleted", ignore = true),
                    @Mapping(target = "refNo", ignore = true),
                    @Mapping(target = "createdAt", ignore = true),
                    @Mapping(target = "updatedAt", ignore = true),
                    @Mapping(target = "pocket", ignore = true),
                    @Mapping(target = "transaction", ignore = true),
            }

    )
    PocketAmount updateDtoToEntity(PocketAmountUpdateDto updateDto);
}
