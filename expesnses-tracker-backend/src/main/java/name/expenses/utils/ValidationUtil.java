package name.expenses.utils;

import lombok.extern.slf4j.Slf4j;
import name.expenses.globals.responses.ResponseDto;

@Slf4j
public class ValidationUtil {

    public static ResponseDto checkNullRefs(Object promoRefNo, Object cashBackId) {
        ResponseDto responseDto = checkNullRef(promoRefNo);
        ResponseDto responseDto1 = checkNullRef(cashBackId);

        if (responseDto != null) {
            return responseDto;

        } else return responseDto1;
    }

    public static ResponseDto checkNullRef(Object ref)
    {
        if (ref == null) {
            log.info("null ref no");
            return ResponseDto.builder()
                    .code(815)
                    .message("null ref number")
                    .build();
        }
        return null;
    }
    public static ResponseDto checkNullEntity(Object entity, String notFound)
    {
        if (entity == null ) {
            log.info("null Entity");
            return ResponseDto.builder()
                    .code(804)
                    .message("null entity")
                    .build();
        }
        return null;
    }
}
