package name.expenses.features.user.service.service_impl;


import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import name.expenses.error.exception.ObjectNotFoundException;
import name.expenses.features.user.dao.RoleRepo;
import name.expenses.features.user.mappers.RoleMapper;
import name.expenses.features.user.models.Role;
import name.expenses.features.user.service.RoleService;
import name.expenses.globals.responses.ResponseDto;

@Slf4j
@Transactional
@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class RoleServiceImpl implements RoleService {
    private final RoleRepo roleRepo;
    private final RoleMapper roleMapper;

    @Override
    public ResponseDto saveRole(String roleName) {
        Role role = roleRepo.findByName(roleName).orElse(null);
        if(role != null){
            return new  ResponseDto("Role Already Exist",true , 200 ,role);
        }
        role = new Role("ROLE_"+roleName);
        roleRepo.save(role);
        return new ResponseDto("New Role Added Successfully",true , 200 ,role);
    }

    @Override
    public ResponseDto getAll() {
        return new ResponseDto("All Roles",true,200,roleMapper.roleName(roleRepo.findAll())) ;
    }

    @Override
    public ResponseDto getById(Long id) {
        return  new ResponseDto("Role Found",true,200,roleRepo.findById(id).orElseThrow(()-> new ObjectNotFoundException("Role Not Found")));
    }
}
