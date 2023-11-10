package gwangjang.server.global.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gwangjang.server.global.response.ErrorCode;
import gwangjang.server.global.response.ErrorResponse;
import gwangjang.server.global.response.SuccessResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.ws.rs.HttpMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.PatternSyntaxException;

@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        log.error("GlobalExceptionHandler start -> {}", ex.getMessage(), ex);

        ServerHttpResponse response =  exchange.getResponse();
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

        } else if (exceptionClass == HttpRequestMethodNotSupportedException.class){
            exchange.getResponse().setStatusCode(HttpStatus.METHOD_NOT_ALLOWED);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            responseBody = new ErrorResponse<>(ErrorCode.METHOD_NOT_ALLOWED);

        }  else if (exceptionClass == HttpMessageNotReadableException.class){
            exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            responseBody = new ErrorResponse<>(ErrorCode.BAD_REQUEST);

        } else if (exceptionClass == PatternSyntaxException.class ){
            exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            responseBody = new ErrorResponse<>(ErrorCode.BAD_REQUEST);

        } else if (exceptionClass == Exception.class){
            exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            responseBody = new ErrorResponse<>(ErrorCode.INTERNAL_SERVER_ERROR);

        }

        // 성공 시 ??
        else {
            exchange.getResponse().setStatusCode(exchange.getResponse().getStatusCode());
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            responseBody = SuccessResponse.create(ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
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
}
