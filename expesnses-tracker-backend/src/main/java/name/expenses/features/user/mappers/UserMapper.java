package name.expenses.features.user.mappers;


import name.expenses.features.user.dtos.request.UserReqDto;
import name.expenses.features.user.dtos.response.UserRespDto;
import name.expenses.features.user.models.Role;
import name.expenses.features.user.models.User;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "JAKARTA",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {
        },
        imports = {LocalDateTime.class})
public abstract class UserMapper {
    @Mappings({
            @Mapping(target = "roles", source = "roles",qualifiedByName = "getRoles")
    })
    public abstract UserRespDto userToUserRespDto(User user);

    @Named("getRoles")
    public List<String> getRoles(Set<Role> roles){
        if (roles == null) {
            return Collections.emptyList();
        }
        return roles.stream().map(Role::getName).toList();
    }

    @Mapping(target = "roles", ignore = true)
    public abstract User userReqDtoToUser(UserReqDto userReqDto);

    public abstract Set<UserRespDto>  userToUserRespDto(Set<User> allByDeletedIsFalse);
}
