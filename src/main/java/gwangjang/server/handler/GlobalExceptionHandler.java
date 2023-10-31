package gwangjang.server.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gwangjang.server.response.ErrorCode;
import gwangjang.server.response.ErrorResponse;
import gwangjang.server.response.SuccessResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    private static final String LOG_FORMAT = "Class : {}, Code : {}, Message : {}";


    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {

        ServerHttpResponse response =  exchange.getResponse();
        List<Class<? extends RuntimeException>> jwtExceptions =
                List.of(SignatureException.class,
                        MalformedJwtException.class,
                        UnsupportedJwtException.class,
                        IllegalArgumentException.class);
        Class<? extends Throwable> exceptionClass = ex.getClass();



        Object responseBody = new HashMap<>();

        if (exceptionClass == ExpiredJwtException.class) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

            responseBody = new ErrorResponse<>(ErrorCode.EXPIRED_JWT);

        } else if (exceptionClass == UnsupportedJwtException.class){
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            responseBody = new ErrorResponse<>(ErrorCode.UNSUPPORTED_TOKEN);

        } else if (exceptionClass == IllegalArgumentException.class){
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            responseBody = new ErrorResponse<>(ErrorCode.INVALID_TOKEN);

        } else if (exceptionClass == SecurityException.class || exceptionClass == MalformedJwtException.class){
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            responseBody = new ErrorResponse<>(ErrorCode.INVALID_JWT_TOKEN);
        }
        // 성공 시
        else {
            exchange.getResponse().setStatusCode(exchange.getResponse().getStatusCode());
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            responseBody = SuccessResponse.create("테스트 로그인에 성공하였습니다.");
//            responseBody.put("data",new ErrorResponse<>(ErrorCode.UNAUTHORIZED));

        }

        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        DataBuffer wrap = null;
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(responseBody);
            wrap = exchange.getResponse().bufferFactory().wrap(bytes);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return exchange.getResponse().writeWith(Flux.just(wrap));
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> handleApplicationException(ApplicationException ex) {
        log.error(LOG_FORMAT, ex.getClass().getSimpleName(), ex.getErrorCode(), ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(ex.getErrorCode(), ex.getMessage());
        return ResponseEntity.status(ex.getHttpStatus().value()).body(errorResponse);
    }


    private ErrorResponse handleException(Exception ex, ErrorCode errorCode, String message, HttpStatus httpStatus, Consumer<String> logger) {
        log.error(LOG_FORMAT, ex.getClass().getSimpleName(), errorCode.getErrorCode(), ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(errorCode, message);
        return errorResponse;
    }



}
