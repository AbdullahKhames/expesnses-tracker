package name.expenses.features.user.service;

import jakarta.ejb.Local;
import jakarta.servlet.http.HttpServletRequest;
import name.expenses.features.user.dtos.request.UserRoleDto;
import name.expenses.globals.responses.ResponseDto;

@Local
public interface UserService {
    ResponseDto getUser(String email);

    ResponseDto addRoleToUser(UserRoleDto userRoleDto);

    ResponseDto softDeleteUser(String email);

    ResponseDto getAll();

    ResponseDto getUserById(Long id);

    ResponseDto activateUser(Long id);

    ResponseDto deActivateUser(Long id);

    ResponseDto validateChangeEmail(String refNo, String token);

    ResponseDto changeEmail(String token, String refNo, String newEmail);

    ResponseDto getUserByRef(String refNo);
}
