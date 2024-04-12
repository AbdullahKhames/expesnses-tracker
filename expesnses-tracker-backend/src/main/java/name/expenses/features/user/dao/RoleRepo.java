package name.expenses.features.user.dao;

import jakarta.ejb.Local;
import name.expenses.features.user.models.Role;

import java.util.List;
import java.util.Optional;
@Local
public interface RoleRepo {


    Optional<Role> findByName(String roleName);
    boolean existsRoleByName(String name);


    Role save(Role role);

    Optional<Role> findById(Long id);

    List<Role> findAll();
}