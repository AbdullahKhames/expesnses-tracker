package name.expenses.features.user.mappers;

import jakarta.inject.Singleton;
import lombok.AllArgsConstructor;
import name.expenses.features.user.models.Role;

import java.util.List;
import java.util.stream.Collectors;

@Singleton
@AllArgsConstructor
public class RoleMapper {
    public List<String> roleName(List<Role> roles){
        return roles.stream().map(r -> r.getName().substring(5)).collect(Collectors.toList());
    }
}
