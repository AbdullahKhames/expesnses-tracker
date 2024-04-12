package name.expenses.features.user.service.service_impl;


import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import name.expenses.features.user.dao.RoleRepo;
import name.expenses.features.user.dao.UserRepo;
import name.expenses.features.user.models.Role;
import name.expenses.features.user.models.User;
import name.expenses.globals.responses.ResponseDto;
import name.expenses.utils.hashing.Hashing;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Transactional
@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class RegisterService {
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    public ResponseDto register(User user) {
        if (userRepo.existsByEmail(user.getEmail())) {
            return ResponseDto.builder()
                    .message("Username already exist")
                    .status(false)
                    .code(2001)
                    .build();
        }
        user.getRoles().forEach(role->
        {

            Role role1 = roleRepo.findByName("ROLE_" + role.getName().toUpperCase()).orElse(null);
            if (role1 == null) {
                role1 = roleRepo.save(new Role("ROLE_" + role.getName().toUpperCase()));
            }
            user.setRoles(Set.of(role1));
        });
        user.setPassword(Hashing.hash(user.getPassword()));
        var savedUser = userRepo.save(user);
        Map<String, String> refNo = new HashMap<>();
        refNo.put("userRefNo", user.getRefNo());
        return ResponseDto.builder()
                .message("Registered Successfully")
                .status(true)
                .code(200)
                .data(refNo)
                .build();
    }
}
