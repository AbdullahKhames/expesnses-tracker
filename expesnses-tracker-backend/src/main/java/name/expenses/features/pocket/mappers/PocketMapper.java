package name.expenses.features.pocket.mappers;




import name.expenses.features.pocket.dtos.request.PocketReqDto;
import name.expenses.features.pocket.dtos.request.PocketUpdateDto;
import name.expenses.features.pocket.dtos.response.PocketRespDto;
import name.expenses.features.pocket.models.Pocket;
import name.expenses.globals.Page;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "JAKARTA",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        uses = {
//            SubPocketMapper.class
        },
        imports = {LocalDateTime.class})
public interface PocketMapper {
    @Mappings(

            {
                    @Mapping(target = "id", ignore = true),
                    @Mapping(target = "deleted", ignore = true),
                    @Mapping(target = "refNo", ignore = true),
                    @Mapping(target = "createdAt", ignore = true),
                    @Mapping(target = "updatedAt", ignore = true),
            }

    )
    Pocket reqDtoToEntity(PocketReqDto entityReqDto);
    PocketRespDto entityToRespDto(Pocket entity);
    Set<PocketRespDto> entityToRespDto(Set<Pocket> entities);
    List<PocketRespDto> entityToRespDto(List<Pocket> entities);
    Page<PocketRespDto> entityToRespDto(Page<Pocket> entitiesPage);

    @Mappings(

            {
                    @Mapping(target = "id", ignore = true),
                    @Mapping(target = "deleted", ignore = true),
                    @Mapping(target = "refNo", ignore = true),
                    @Mapping(target = "createdAt", ignore = true),
                    @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())"),

            }

    )
    void update(@MappingTarget Pocket entity, PocketUpdateDto entityUpdateDto);
    @Mappings(

            {
                    @Mapping(target = "id", ignore = true),
                    @Mapping(target = "deleted", ignore = true),
                    @Mapping(target = "refNo", ignore = true),
                    @Mapping(target = "createdAt", ignore = true),
                    @Mapping(target = "updatedAt", ignore = true),
            }

    )
    Pocket reqEntityToEntity(PocketUpdateDto newPocket);
}
