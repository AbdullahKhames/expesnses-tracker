package name.expenses.repostitories;



import name.expenses.models._2auth;

import java.util.Optional;
public interface _2authRepo {

    Optional<_2auth> findByPhoneAndExpiredFalse(String phone);
    _2auth findByPhoneAndOtp(String phone, String otp);
    boolean existsByPhoneAndTokenAndRfeNoAndDeviceId(String phone, String token, String refNo, String deviceId);
    _2auth findByPhoneAndOtpAndRfeNoAndExpiredFalse(String phone, String otp ,String refNo);
    Boolean existsByPhoneAndExpiredFalse(String phone);

    _2auth save(_2auth auth);

    void delete(_2auth auth);

    Optional<_2auth> findById(Long id);

    boolean existsByEmailAndExpiredFalse(String email);

    Optional<_2auth> findByEmailAndExpiredFalse(String email);

    _2auth findByEmailAndOtpAndRfeNoAndExpiredFalse(String email, String otp, String rfeNo);

    _2auth update(_2auth auth);

    _2auth findByEmailAndOtpAndExpiredFalse(String email, String otp);

    _2auth findByEmailAndTokenAndExpiredFalse(String email, String token);
}