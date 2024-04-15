package name.expenses.features.user.mappers;

import name.expenses.features.user.dtos.request._2authDto;
import name.expenses.features.user.dtos.response.OtpResponseDto;
import name.expenses.features.user.models._2auth;
import org.mapstruct.*;

import java.util.Set;

@Mapper(componentModel = "JAKARTA",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {
        })
public interface _2authMapper {

    @Mapping(source = "type" , target = "type")
    _2authDto entityToDto(_2auth auth);

    @InheritInverseConfiguration
    _2auth dtoToEntity(_2authDto authDto);

    Set<_2authDto> listEntitiesToDto(Set<_2auth> auths);
    Set<_2auth> listDtoToEntites(Set<_2authDto> authDtos);

    OtpResponseDto map(_2auth auth);
}