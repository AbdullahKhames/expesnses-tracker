package name.expenses.features.user.service.service_impl;



import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.SecurityContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import name.expenses.error.exception.APIException;
import name.expenses.error.exception.ErrorCode;
import name.expenses.error.exception.GeneralFailureException;
import name.expenses.error.exception.ObjectNotFoundException;
import name.expenses.features.customer.models.Customer;
import name.expenses.features.customer.service.CustomerService;
import name.expenses.features.user.dao.*;
import name.expenses.features.user.dtos.request.LoginRequest;
import name.expenses.features.user.dtos.request.UserReqDto;
import name.expenses.features.user.dtos.response.AuthResponse;
import name.expenses.features.user.mappers.UserMapper;
import name.expenses.features.user.models.*;
import name.expenses.globals.responses.ResponseDto;
import name.expenses.utils.ResponseDtoBuilder;
import name.expenses.utils.hashing.Hashing;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
@Transactional
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class AuthService {
    private final UserRepo userRepo;
    private final _2authRepo authRepo;
    private final TokenRepo tokenRepo;
    private final RoleRepo roleRepo;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final GroupDao groupDao;
    private final CustomerService customerService;
    @Context
    SecurityContext securityContext;
    public boolean verified(String email, String token, Type type) {
        if (email == null || token == null ||  type == null) {
            return false;
        }
        Token savedToken = tokenRepo.findByToken(token).orElseThrow(() -> new ObjectNotFoundException("Token not found"));
        boolean isTokenValid = !savedToken.isExpired() && !savedToken.isRevoked();
        log.info("isTokenValid :{}", isTokenValid);

        try{
            User user = userRepo.findUserByEmailAndDeletedIsFalse(email).orElseThrow(() -> new ObjectNotFoundException("User Not Found"));
            if (!user.getTokens().contains(savedToken)){
                throw new GeneralFailureException(ErrorCode.INVALID_ACCOUNTNUMBER.getErrorCode(),
                        Map.of("error", "invalid token for user"));
            }
            Claims claims = jwtService.extractAllClaims(token);
            String deviceId = claims.get("deviceId").toString();
            _2auth auth = authRepo.findByEmailAndTokenAndExpiredFalse(email, token);
            boolean exists = auth != null;
            if (exists && !Objects.equals(deviceId, auth.getDeviceId())){
                throw new GeneralFailureException(ErrorCode.INVALID_ACCOUNTNUMBER.getErrorCode(),
                        Map.of("error", "invalid device id for token"));
            }
            String type1 = claims.get("type").toString();
            if (!type1.equals(type.toString())){
                throw new GeneralFailureException(ErrorCode.INVALID_ACCOUNTNUMBER.getErrorCode(),
                        Map.of("error", "invalid token type"));
            }
            revokeToken(token);
            return exists && isTokenValid && (type1.equals(type.toString()));
        }catch (Exception exception){
            throw new GeneralFailureException(ErrorCode.INVALID_VERIFICATION_TOKEN.getErrorCode(),
                    Map.of("error", "invalid verification token"));
        }

    }


    public ResponseDto registerWithOtp(UserReqDto userReqDto) {
        if (userReqDto.getVerificationToken() == null) {
            throw new APIException(ErrorCode.INVALID_VERIFICATION_TOKEN.getErrorCode());
        }
        log.info("in register with phone ");
        String email = userReqDto.getEmail();
        if (!verified(email, userReqDto.getVerificationToken() , Type.REGISTER)) {
            return ResponseDto.builder()
                    .status(false)
                    .message("not Verified")
                    .code(403)
                    .build();
        }
        if (userRepo.existsByEmail(email)) {
            return ResponseDto.builder()
                    .message("email already exist")
                    .status(false)
                    .code(2001)
                    .build();
        }
        String token = userReqDto.getVerificationToken();
        revokeToken(token);
        return register(userReqDto, false);
    }

    public ResponseDto register(UserReqDto request, boolean fromOtherController) {
        if (request == null){
            return ResponseDto.builder()
                    .message("error must provide profile type")
                    .status(false)
                    .code(2010)
                    .build();
        }
        if (userRepo.existsByEmail(request.getEmail())) {
            return ResponseDto.builder()
                    .message("Email already exist")
                    .status(false)
                    .code(2001)
                    .build();
        }
        User user = userMapper.userReqDtoToUser(request);
        Role role = roleRepo.findByName("ROLE_" + request.getRole().toUpperCase()).orElse(null);
        if (role == null) {
            role = roleRepo.save(new Role("ROLE_" + request.getRole().toUpperCase()));
        }
        user.setRoles(Set.of(role));
        user.setPassword(Hashing.hash(request.getPassword()));
        User savedUser = userRepo.save(user);
        if (!fromOtherController && request.getRole().equalsIgnoreCase("CUSTOMER")) {
            Customer customer = new Customer();
            customer.setUser(savedUser);
            return ResponseDto.builder()
                    .message("Registered Successfully")
                    .status(true)
                    .code(200)
                    .data(customerService.create(customer))
                    .build();
        }
//        Group group = new Group(user.getEmail(), "BASIC");
//        groupDao.save(group);
        return ResponseDto.builder()
                .message("Registered Successfully")
                .status(true)
                .code(200)
                .data(savedUser)
                .build();
    }
    public void saveUserToken(User user, String jwtToken, TokenType tokenType) {
        Token token = null;
        if(user != null){
            token = tokenRepo.findTokenByUser_IdAndTokenType(user.getId(), tokenType).orElse(null);
        }
        if (token == null) {
            token = Token.builder()
                    .user(user)
                    .token(jwtToken)
                    .tokenType(tokenType)
                    .expired(false)
                    .expired(false)
                    .build();
        } else {
            token.setExpired(false);
            token.setRevoked(false);
            token.setToken(jwtToken);
        }
        tokenRepo.save(token);
    }

    public void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepo.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty()) {
            return;
        }
        validUserTokens.forEach(t -> {
            t.setExpired(true);
            t.setRevoked(true);
        });
        tokenRepo.saveAll(validUserTokens);
    }
    private void revokeToken(String token) {

        Token vToken = tokenRepo.findByToken(token).orElse(null);
        if(vToken != null){
            vToken.setRevoked(true);
            vToken.setExpired(true);
            tokenRepo.save(vToken);
        }
    }

    public void removeAllUserTokens(User user) {
        var validUserTokens = tokenRepo.findAllTokenByUser(user.getId());
        if (validUserTokens.isEmpty()) {
            return;
        }
        tokenRepo.deleteAll(validUserTokens);
    }


    public ResponseDto generateTokenWithRefreshToken(String refreshToken) {
        String uuid = jwtService.extractAllClaims(refreshToken).get("UUID").toString();
        User user = userRepo.findByRefAndDeletedIsFalse(uuid).orElseThrow(() -> new ObjectNotFoundException("User Not Found"));
        var jwtToken = jwtService.generateToken(user);
        var jwtRefreshToken = jwtService.generateRefreshToken(user);
        String message = "Token Generated Successfully";


        return ResponseDto.builder()
                .status(true)
                .message(message)
                .code(200)
                .data(AuthResponse.builder()
                        .data(userMapper.userToUserRespDto(user))
                        .accessToken(jwtToken)
                        .refreshToken(jwtRefreshToken)
                        .build())
                .build();

    }
    public ResponseDto refreshToken(
            HttpServletRequest request,
            String token) throws IOException {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String username;


        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseDtoBuilder.getErrorResponse(810, "auth header cannot be null");
        }
//       extract jwt token "Bearer "-> 7 chars
        refreshToken = authHeader.substring(7);
//        extract username from JWT token
        username = jwtService.extractUsername(refreshToken);
        log.info("work2");
        log.info("user name {}", username);
//        check if the user not auth yet
        var authResponse = ResponseDto.builder()
                .message("not valid")
                .code(2040)
                .status(false)
                .build();

        if (username != null) {
            var user = userRepo.findUserByEmailAndDeletedIsFalse(username).orElseThrow(() -> new ObjectNotFoundException("User Not Found"));
            log.info("work1");
            var isTokenValid = tokenRepo.findByToken(refreshToken)
                    .map(t -> !t.isExpired() && !t.isRevoked() && t.getTokenType() == TokenType.REFRESH)
                    .orElse(false);
            log.info("is ref valid {}", isTokenValid);
            if (jwtService.isTokenValid(refreshToken, user) && isTokenValid) {
                //check if acc token and ref for the same user
                if (new HashSet<>(user
                        .getTokens()
                        .stream()
                        .map(Token::getToken)
                        .collect(Collectors.toList()))
                        .containsAll(List.of(token, refreshToken))) {
                    var accessToken = jwtService.generateToken(user);
                    log.info("work");

                    saveUserToken(user, accessToken, TokenType.BEARER);
                    saveUserToken(user, refreshToken, TokenType.REFRESH);
                    authResponse = ResponseDto.builder()
                            .data(AuthResponse.builder()
                                    .accessToken(accessToken)
                                    .refreshToken(refreshToken)
                                    .build())
                            .code(200)
                            .status(true)
                            .build();
                    return authResponse;
                }
            }

        }
        return authResponse;
    }

    public ResponseDto resetAccount(LoginRequest loginRequest) {
        if (loginRequest.getToken() == null) {
            throw new APIException(ErrorCode.INVALID_VERIFICATION_TOKEN.getErrorCode());
        }
        if (!verified(loginRequest.getEmail(), loginRequest.getToken() ,Type.RESET_PASSWORD)) {
            return ResponseDto.builder()
                    .status(false)
                    .message("not Verified")
                    .code(403)
                    .build();
        }
        User user = userRepo.findUserByEmailAndDeletedIsFalse(loginRequest.getEmail()).orElseThrow(() -> new ObjectNotFoundException("User Not Found"));

        user.setPassword(Hashing.hash(loginRequest.getPassword()));
        userRepo.update(user);

        return phoneLogin(loginRequest);
    }
    public ResponseDto phoneLogin(LoginRequest loginRequest) {
        String message;
        boolean status = true;


        User user = userRepo.findUserByEmailAndDeletedIsFalse(loginRequest.getEmail()).orElseThrow(() -> new ObjectNotFoundException("User Not Found"));
        if (!Hashing.verify(loginRequest.getPassword(), user.getPassword())) {
            throw new GeneralFailureException(ErrorCode.USER_IS_NOT_FOUND.getErrorCode(),
                    Map.of("error ", " user is not found with given email"));
        }
        boolean existByDeviceId = userRepo.existByIdAndDeviceId(user.getId(), loginRequest.getDeviceId());
        if (existByDeviceId){
            throw new GeneralFailureException(ErrorCode.USER_IS_NOT_FOUND.getErrorCode(),
                    Map.of("error ", " someone is already logged in to this device please logout first"));
        }
        if (!verified(loginRequest.getEmail(), loginRequest.getToken() , Type.LOGIN)) {
            return ResponseDto.builder()
                    .status(false)
                    .message("not Verified")
                    .code(403)
                    .build();
        }
        if (user.isLoggedIn() && user.getDeviceId() != null ){
            if (user.getDeviceId().equals(loginRequest.getDeviceId())){
                return performUserLogin(loginRequest, user, status);
            }else {
                throw new GeneralFailureException(ErrorCode.USER_IS_NOT_FOUND.getErrorCode(),
                        Map.of("error ", " user already logged in in another device"));
            }
        }

        user.setLoggedIn(true);
        user.setDeviceId(loginRequest.getDeviceId());

        return performUserLogin(loginRequest, user, status);
    }

    private ResponseDto performUserLogin(LoginRequest loginRequest, User user, boolean status) {
        String message;
        String token = loginRequest.getToken();
        revokeToken(token);
        var savedUSer = userRepo.update(user);

        var jwtToken = jwtService.generateToken(savedUSer);

        saveUserToken(savedUSer, jwtToken, TokenType.BEARER);
        var jwtRefreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUSer, jwtRefreshToken, TokenType.REFRESH);

        message = "User " + user.getUsername() + " LoggedIn Successfully";


        return ResponseDto.builder()
                .status(status)
                .message(message)
                .code(200)
                .data(AuthResponse.builder()
                        .data(userMapper.userToUserRespDto(user))
                        .accessToken(jwtToken)
                        .refreshToken(jwtRefreshToken)
                        .build())
                .build();
    }

    public ResponseDto logout() {
        User user;
        try {
            user= (User) securityContext.getUserPrincipal();
        }catch (Exception ex){
            user = userRepo.findUserByEmailAndDeletedIsFalse(securityContext.getUserPrincipal().getName())
                    .orElseThrow(() -> new ObjectNotFoundException("user not found"));
        }

        user.setLoggedIn(false);
        user.setDeviceId(null);
        revokeAllUserTokens(user);
        userRepo.update(user);
        return ResponseDto.builder()
                .status(true)
                .message("User Logged Out Successfully")
                .code(200)
                .build();
    }
    public ResponseDto logoutWithRef(String refNo) {
        User user = userRepo.findByRefAndDeletedIsFalse(refNo).orElseThrow(() -> new ObjectNotFoundException("User Not Found"));
        user.setLoggedIn(false);
        user.setDeviceId(null);
        userRepo.update(user);
        revokeAllUserTokens(user);
        return ResponseDto.builder()
                .status(true)
                .message("User Logged Out Successfully")
                .code(200)
                .build();
    }
}