package name.expenses.features.user.service.service_impl;


import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import name.expenses.error.exception.ErrorCode;
import name.expenses.error.exception.GeneralFailureException;
import name.expenses.error.exception.ObjectNotFoundException;
import name.expenses.features.user.dao.UserRepo;
import name.expenses.features.user.dao._2authRepo;
import name.expenses.features.user.dtos.request.ValidAuthDto;
import name.expenses.features.user.dtos.request._2authDto;
import name.expenses.features.user.dtos.response.OtpResponseDto;
import name.expenses.features.user.mappers._2authMapper;
import name.expenses.features.user.models.TokenType;
import name.expenses.features.user.models.Type;
import name.expenses.features.user.models.User;
import name.expenses.features.user.models._2auth;
import name.expenses.features.user.service._2authServices;
import name.expenses.globals.responses.ResponseDto;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Date;
import java.util.Map;

@Singleton
@Transactional
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class _2authServicesImp implements _2authServices {

    private final _2authRepo authRepo;
    private final _2authMapper authMapper;
    private final UserRepo userRepo;
    private final AuthService authService;
    private final JwtService jwtService;


    public String generateOTP(){
        return  RandomStringUtils.random(4,false,true);
    }

    @Override
    public ResponseDto add_2auth(_2authDto authDto) {
        if(authDto.getType().equals(Type.LOGIN)){
            User user =userRepo.findUserByEmailAndDeletedIsFalse(authDto.getEmail()).orElse(null) ;
            if (user==null){
                return new ResponseDto("Email Not Found",false,2002, null);
            } else if(user.isLoggedIn() && !authDto.getDeviceId().equals(user.getDeviceId())){
                return new ResponseDto("User already Logged in Logout first",false,2007, null);
            }
        } else if (authDto.getType() == (Type.CHANGE_EMAIL)){
            if(userRepo.existsByEmail(authDto.getEmail())){
                _2auth auth = authMapper.dtoToEntity(authDto);
                auth.setOtp(generateOTP());
                authRepo.save(auth);
                OtpResponseDto otpResponseDto = authMapper.map(auth);
                return  new ResponseDto("auth Saved Successfully",true,201, otpResponseDto);
            }
        }else if(authDto.getType() == (Type.REGISTER)){
            if(userRepo.existsByEmail(authDto.getEmail())){
                return new ResponseDto("Email Already Exist",false,2001, null);
            }
        } else if (authDto.getType() == (Type.RESET_PASSWORD)) {
            User user =userRepo.findUserByEmailAndDeletedIsFalse(authDto.getEmail()).orElseThrow(()->new ObjectNotFoundException("User not Found")) ;

        }
        if(authRepo.existsByEmailAndExpiredFalse(authDto.getEmail())){
            _2auth auth = authRepo.findByEmailAndExpiredFalse(authDto.getEmail()).orElseThrow( () -> new ObjectNotFoundException("Auth Object Not Found"));
            expireOtp(auth);
//            remove_2authByEmail(authDto.getEmail());
        }
        log.info("{}",authDto.getType());
        _2auth auth = authMapper.dtoToEntity(authDto);
        log.info("{}",auth.getType());

        auth.setOtp(generateOTP());

        authRepo.save(auth);
        OtpResponseDto otpResponseDto = authMapper.map(auth);
        return  new ResponseDto("auth Saved Successfully",true,201, otpResponseDto);
    }

    @Override
    public ResponseDto get_2authById(Long id) {
        _2auth auth = authRepo.findById(id).orElseThrow( () -> new ObjectNotFoundException("Auth Object Not Found"));

        _2authDto authDto = authMapper.entityToDto(auth);
        return new ResponseDto("Auth get Successfully", true, 200, authDto);
    }

    @Override
    public ResponseDto get_2authByEmail(String email) {
        _2auth auth = authRepo.findByEmailAndExpiredFalse(email).orElseThrow( () -> new ObjectNotFoundException("Auth Object Not Found"));

        _2authDto authDto = authMapper.entityToDto(auth);
        return new ResponseDto("Auth get Successfylly", true, 200, authDto);
    }

    @Override
    public void remove_2authById(Long id) {
        _2auth auth = authRepo.findById(id).orElseThrow( () -> new ObjectNotFoundException("Auth Object Not Found"));
        authRepo.delete(auth);
    }

    @Override
    public void remove_2authByEmail(String email) {
        _2auth auth = authRepo.findByEmailAndExpiredFalse(email).orElseThrow( () -> new ObjectNotFoundException("Auth Object Not Found"));
        authRepo.delete(auth);
    }


    @Override
    public ResponseDto codeVerification(ValidAuthDto authDto) {
        log.info("authDto {} ",authDto);

//        _2auth auth = authRepo.findByEmailAndOtp(authDto.getEmail(),authDto.getOtp());
        _2auth auth = authRepo.findByEmailAndOtpAndRfeNoAndExpiredFalse(authDto.getEmail(),authDto.getOtp(),authDto.getRefNo());
        if(auth != null){
            if(DateIsExpired(auth.getCreatedAt())){
                expireOtp(auth);

//                authRepo.delete(auth);
                return new ResponseDto("expired",false,2005,null);

            }
            if (!auth.getDeviceId().equals(authDto.getDeviceId())){
                throw new GeneralFailureException(ErrorCode.INVALID_ACCOUNTNUMBER.getErrorCode(),
                        Map.of("error", "device id doesn't match original device id please re ask for otp on this device or use old device"));
            }
//            authRepo.delete(auth);
            expireOtp(auth);
//            User user = userRepo.findUserByEmailAndDeletedIsFalse(authDto.getEmail()).orElseThrow(()->new ObjectNotFoundException("User Not Found"));
            User user = userRepo.findUserByEmailAndDeletedIsFalse(authDto.getEmail()).orElse(null);
            log.info("uuussser");
            String token = jwtService.generateVerificationToken(authDto.getRefNo(), auth.getDeviceId(), auth.getType());
            authService.saveUserToken(user, token, TokenType.VERIFICATION);
            auth.setToken(token);
            authRepo.update(auth);
            return new ResponseDto("succeed",true,200, Map.of("token", token));
        }
        return new ResponseDto("failed please re ask for otp",false,2006,null);
    }
    private void expireOtp(_2auth auth){
        auth.setExpired(true);
        authRepo.update(auth);
    }


    public boolean DateIsExpired(Date createdAt) {
        Date currentDate = new Date();
        int diffInTime = (int)( (currentDate.getTime() - createdAt.getTime()) / (1000 * 60));
        return diffInTime > 2;
    }


}
