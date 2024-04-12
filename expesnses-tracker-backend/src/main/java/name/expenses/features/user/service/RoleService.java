package name.expenses.features.user.service;

import jakarta.ejb.Local;
import name.expenses.globals.responses.ResponseDto;
@Local
public interface RoleService {
    ResponseDto saveRole(String roleName) ;
    ResponseDto getAll();

    ResponseDto getById(Long id);
}