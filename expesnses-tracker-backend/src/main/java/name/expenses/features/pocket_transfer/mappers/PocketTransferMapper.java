package name.expenses.features.pocket_transfer.mappers;


import name.expenses.features.pocket_transfer.dtos.request.PocketTransferReqDto;
import name.expenses.features.pocket_transfer.dtos.request.PocketTransferUpdateDto;
import name.expenses.features.pocket_transfer.dtos.response.PocketTransferRespDto;
import name.expenses.features.pocket_transfer.models.PocketTransfer;
import name.expenses.globals.Page;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "JAKARTA",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {
                PocketAmountMapper.class,
        },
        imports = {LocalDateTime.class})
public interface PocketTransferMapper {
    @Mappings(

            {
                    @Mapping(target = "id", ignore = true),
                    @Mapping(target = "deleted", ignore = true),
                    @Mapping(target = "refNo", ignore = true),
                    @Mapping(target = "createdAt", ignore = true),
                    @Mapping(target = "updatedAt", ignore = true),
                    @Mapping(target = "senderPocketAmount", ignore = true),
                    @Mapping(target = "receiverPocketAmounts", ignore = true),
            }

    )
    PocketTransfer reqDtoToEntity(PocketTransferReqDto entityReqDto);
    PocketTransferRespDto entityToRespDto(PocketTransfer entity);
    Set<PocketTransferRespDto> entityToRespDto(Set<PocketTransfer> entities);
    List<PocketTransferRespDto> entityToRespDto(List<PocketTransfer> entities);
    Page<PocketTransferRespDto> entityToRespDto(Page<PocketTransfer> entitiesPage);

    @Mappings(

            {
                    @Mapping(target = "id", ignore = true),
                    @Mapping(target = "deleted", ignore = true),
                    @Mapping(target = "refNo", ignore = true),
                    @Mapping(target = "createdAt", ignore = true),
                    @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())"),
                    @Mapping(target = "senderPocketAmount", ignore = true),
                    @Mapping(target = "receiverPocketAmounts", ignore = true),
            }

    )
    void update(@MappingTarget PocketTransfer entity, PocketTransferUpdateDto entityUpdateDto);
}
