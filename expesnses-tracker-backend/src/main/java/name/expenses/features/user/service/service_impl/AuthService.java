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
import name.expenses.features.user.dao.RoleRepo;
import name.expenses.features.user.dao.TokenRepo;
import name.expenses.features.user.dao.UserRepo;
import name.expenses.features.user.dao._2authRepo;
import name.expenses.features.user.dtos.request.LoginRequest;
import name.expenses.features.user.dtos.request.UserReqDto;
import name.expenses.features.user.dtos.response.AuthResponse;
import name.expenses.features.user.mappers.UserMapper;
import name.expenses.features.user.models.*;
import name.expenses.globals.responses.ResponseDto;
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
    @Context
    SecurityContext securityContext;
    public boolean verified(String phone, String token, Type type) {
        if (phone == null || token == null ||  type == null) {
            return false;
        }
        var isTokenValid = tokenRepo.findByToken(token)
                .map(t -> !t.isExpired() && !t.isRevoked())
                .orElse(false);
        log.info("isTokenValid :{}", isTokenValid);

        Claims claims = jwtService.extractAllClaims(token);
        String otpRef = claims.get("ref").toString();
        String deviceId = claims.get("deviceId").toString();
        boolean exists = authRepo.existsByPhoneAndTokenAndRfeNoAndDeviceId(phone, token, otpRef, deviceId);
        String type1 = claims.get("type").toString();
        return exists && isTokenValid && (type1.equals(type.toString()));
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
        return register(userReqDto);
    }

    public ResponseDto register(UserReqDto request) {
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
        var savedUser = userRepo.save(user);
        var jwtToken = jwtService.generateToken(user);

        saveUserToken(savedUser, jwtToken, TokenType.BEARER);
        var jwtRefreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwtRefreshToken, TokenType.REFRESH);

        Map<String, String> refNo = new HashMap<>();
        refNo.put("userRefNo", user.getRefNo());

        return ResponseDto.builder()
                .message("Registered Successfully")
                .status(true)
                .code(200)
                .data(refNo)
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
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response,
            String token) throws IOException {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String username;


        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
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
        response.setContentType("application/json");

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
                    new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
                }
            }

        }
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);

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

        user.setLoggedIn(true);
        user.setDeviceId(loginRequest.getDeviceId());

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