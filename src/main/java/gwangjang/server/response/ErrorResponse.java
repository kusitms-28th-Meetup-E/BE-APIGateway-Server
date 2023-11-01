package gwangjang.server.response;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class ErrorResponse<T> {
    private Boolean isSuccess;
//    private LocalDateTime timeStamp;
    private String errorCode;
    private String message;

    public ErrorResponse(String errorCode, String message) {
        this.isSuccess=false;
//        this.timeStamp = LocalDateTime.now().withNano(0);
        this.errorCode = errorCode;
        this.message = message;
    }

    public ErrorResponse(ErrorCode errorCode, String message) {
        this.isSuccess=false;
//        this.timeStamp = LocalDateTime.now().withNano(0);
        this.errorCode=errorCode.getErrorCode();
        this.message=message;
    }
    public ErrorResponse(ErrorCode errorCode) {
        this.isSuccess=false;
//        this.timeStamp = LocalDateTime.now().withNano(0);
        this.errorCode=errorCode.getErrorCode();
        this.message=errorCode.getMessage();
    }
}