package name.expenses.features.user.mappers;


import name.expenses.features.user.dtos.request.UserReqDto;
import name.expenses.features.user.dtos.request.UserUpdateDto;
import name.expenses.features.user.dtos.response.UserRespDto;
import name.expenses.features.user.models.User;
import name.expenses.globals.Page;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "JAKARTA",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        uses = {
        },
        imports = {LocalDateTime.class})
public interface UserMapper {
    @Mappings(

            {
                    @Mapping(target = "id", ignore = true),
                    @Mapping(target = "deleted", ignore = true),
                    @Mapping(target = "password", ignore = true),
                    @Mapping(target = "refNo", ignore = true),
                    @Mapping(target = "createdAt", ignore = true),
                    @Mapping(target = "updatedAt", ignore = true),
            }

    )
    User reqDtoToEntity(UserReqDto entityReqDto);
    UserRespDto entityToRespDto(User entity);
    Set<UserRespDto> entityToRespDto(Set<User> entities);
    List<UserRespDto> entityToRespDto(List<User> entities);
    Page<UserRespDto> entityToRespDto(Page<User> entitiesPage);

    @Mappings(

            {
                    @Mapping(target = "id", ignore = true),
                    @Mapping(target = "deleted", ignore = true),
                    @Mapping(target = "refNo", ignore = true),
                    @Mapping(target = "createdAt", ignore = true),
                    @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())"),

            }

    )
    void update(@MappingTarget User entity, UserUpdateDto entityUpdateDto);
    @Mappings(

            {
                    @Mapping(target = "id", ignore = true),
                    @Mapping(target = "deleted", ignore = true),
                    @Mapping(target = "refNo", ignore = true),
                    @Mapping(target = "createdAt", ignore = true),
                    @Mapping(target = "updatedAt", ignore = true),
            }

    )
    User reqEntityToEntity(UserUpdateDto newUser);
}
