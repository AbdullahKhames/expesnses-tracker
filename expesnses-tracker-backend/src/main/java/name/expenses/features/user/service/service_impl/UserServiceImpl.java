package name.expenses.features.user.service.service_impl;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import name.expenses.error.exception.ObjectNotFoundException;
import name.expenses.features.user.dao.RoleRepo;
import name.expenses.features.user.dao.UserRepo;
import name.expenses.features.user.dtos.request.*;
import name.expenses.features.user.dtos.response.AuthResponse;
import name.expenses.features.user.dtos.response.OtpResponseDto;
import name.expenses.features.user.mappers.UserMapper;
import name.expenses.features.user.models.Role;
import name.expenses.features.user.models.TokenType;
import name.expenses.features.user.models.Type;
import name.expenses.features.user.models.User;
import name.expenses.features.user.service.UserService;
import name.expenses.features.user.service._2authServices;
import name.expenses.globals.responses.ResponseDto;

import java.util.Objects;

@Singleton
@Transactional
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final _2authServices authServices;
    private final AuthService authService;
    @Override
    public ResponseDto getUser(String email) {
        User user = userRepo.findUserByEmailAndDeletedIsFalse(email).orElseThrow(()-> new ObjectNotFoundException("UserName Doesn't Exist"));
        return new ResponseDto("User Found",true ,200,userMapper.userToUserRespDto(user));
    }
    @Override
    public ResponseDto getUserById(Long id) {
        User user = userRepo.findUserByIdAndDeletedIsFalse(id).orElseThrow(()-> new ObjectNotFoundException("User Doesn't Exist"));
        return new ResponseDto("User Found",true ,200,userMapper.userToUserRespDto(user));
    }

    @Override
    public ResponseDto addRoleToUser(UserRoleDto userRoleDto) {
        User user= userRepo.findUserByEmailAndDeletedIsFalse(userRoleDto.getEmail())
                .orElseThrow(()->new ObjectNotFoundException("email Doesn't Exist"));
        Role role = new Role();

        if(!roleRepo.existsRoleByName("ROLE_"+userRoleDto.getRoleName())){
            role.setName("ROLE_"+userRoleDto.getRoleName().toUpperCase());
            role = roleRepo.save(role);
        }else {
            role = roleRepo.findByName("ROLE_"+userRoleDto.getRoleName()).orElseThrow();
        }
        user.getRoles().add(role);
        userRepo.update(user);
        return new ResponseDto("Role Added to User Successfully",true ,200,userMapper.userToUserRespDto(user));
    }

    @Override
    public ResponseDto softDeleteUser(String email) {
        User user = userRepo.findUserByEmailAndDeletedIsFalse(email).orElseThrow(()-> new ObjectNotFoundException("User doesn't exist"));

        user.setDeleted(true);
        userRepo.update(user);
        return new ResponseDto("User Deleted Successfully",true , 200 , userMapper.userToUserRespDto(user) );
    }

    @Override
    public ResponseDto getAll() {
        return new ResponseDto("All users" , true , 200 ,userMapper.userToUserRespDto(userRepo.findAllByDeletedIsFalse())) ;
    }

    @Override
    public ResponseDto activateUser(Long id) {
        User user = userRepo.findUserByIdAndDeletedIsFalse(id).orElseThrow(()-> new ObjectNotFoundException("User doesn't exist"));
        if(user.isVerified()){
            return new ResponseDto("User Already Activated" , true , 200 ,userMapper.userToUserRespDto(user));
        }
        user.setVerified(true);
        userRepo.update(user);


        return new ResponseDto("User Activated Successfully" , true , 200 ,userMapper.userToUserRespDto(user)) ;

    }

    @Override
    public ResponseDto deActivateUser(Long id) {
        User user = userRepo.findUserByIdAndDeletedIsFalse(id).orElseThrow(()-> new ObjectNotFoundException("User doesn't exist"));
        if(!user.isVerified()){
            return new ResponseDto("User Already deActivated" , true , 200 ,userMapper.userToUserRespDto(user));
        }
        user.setVerified(false);
        userRepo.update(user);

        return new ResponseDto("User deActivated Successfully" , true , 200 ,userMapper.userToUserRespDto(user)) ;

    }

    @Override
    public ResponseDto validateChangeEmail(String refNo, String token) {
        User user =userRepo.findByRefAndDeletedIsFalse( refNo).orElseThrow(()-> new ObjectNotFoundException("user not found with that credentials"));
        _2authDto authDto = new _2authDto();
        authDto.setEmail(user.getEmail());
        authDto.setDeviceId(user.getDeviceId());
        authDto.setType(Type.CHANGE_EMAIL);

        return new ResponseDto("added  otp", true, 200
                , new ValidatePhoneDto((OtpResponseDto) authServices.add_2auth(authDto).getData(), token));
    }

    @Override
    public ResponseDto changeEmail(String token, String refNo,String email) {
//        String oldToken = getOldTokenFromRequest(request);

        User user = userRepo.findByRefAndDeletedIsFalse(refNo)
                .orElseThrow(()-> new ObjectNotFoundException("user not found with that credentials"));
        if (!jwtService.isTokenValid(token, user)){
            return new ResponseDto("token not valid", false, 2041
                    , null);
        }
        if (user.getEmail().equals( email)){
            return new ResponseDto("same email", false, 2040
                    , null);
        }
        if (!authService.verified(user.getEmail(),token ,Type.CHANGE_EMAIL)){
            return new ResponseDto("not verified", false, 403
                    , null);
        }

        user.setEmail(email);
        authService.removeAllUserTokens(user);
        userRepo.update(user);
        var jwtToken = jwtService.generateToken(user);

        authService.saveUserToken(user, jwtToken, TokenType.BEARER);
        var jwtRefreshToken = jwtService.generateRefreshToken(user);
        authService.saveUserToken(user, jwtRefreshToken,TokenType.REFRESH);
        return ResponseDto.builder()
                .status(true)
                .message("user" + user.getEmail() +"changed phone successfully")
                .code(200)
                .data(AuthResponse.builder()
                        .data(userMapper.userToUserRespDto(user))
                        .accessToken(jwtToken)
                        .refreshToken(jwtRefreshToken)
                        .build())
                .build();
    }

    @Override
    public ResponseDto getUserByRef(String refNo) {
        User user = userRepo.findUserByRefAndDeletedIsFalse(refNo)
                .orElseThrow(()-> new ObjectNotFoundException("User Doesn't Exist"));
        return ResponseDto.builder()
                .status(true)
                .message("user " + user.getEmail() +" was fetched successfully")
                .code(200)
                .data(userMapper.userToUserRespDto(user))
                .build();
    }

}