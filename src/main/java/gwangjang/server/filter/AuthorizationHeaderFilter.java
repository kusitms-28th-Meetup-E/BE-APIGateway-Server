package gwangjang.server.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import gwangjang.server.response.ErrorCode;
import gwangjang.server.response.ErrorResponse;
import gwangjang.server.response.SuccessResponse;
import gwangjang.server.security.JwtTokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.InvalidClaimException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.NoSuchElementException;

@Component
@Slf4j
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public AuthorizationHeaderFilter(JwtTokenProvider jwtTokenProvider) {
        super(Config.class);
        this.jwtTokenProvider = jwtTokenProvider;
    }

    static class Config {

    }

    // 사용자의 헤더에 Authorization 값이 없거나 유효한 토큰이 아니라면 사용자에게 권한이 없다는 401 Unauthorized 코드를 반환한다.
    @Override
    public GatewayFilter apply(Config config) {
        //prefeilter
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            log.info("AuthorizationHeaderFilter Start: request -> {}", exchange.getRequest());

            HttpHeaders headers = request.getHeaders();
            if (!headers.containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, NoSuchFieldException.class);
            }

            log.info("authorizationHeader.get-start");
            String authorizationHeader = headers.get(HttpHeaders.AUTHORIZATION).get(0);
            log.info("authorizationHeader.get-end");

            log.info("validateJwtToken-start");
            // JWT 토큰 판별
            String token = authorizationHeader.replace("Bearer", "");

            jwtTokenProvider.validateJwtToken(token);
            log.info("validateJwtToken-end");

            log.info("jwtTokenProvider.getUserId-start");

            String subject = jwtTokenProvider.getUserId(token);

//            if (!jwtTokenProvider.getRoles(token).contains("USER")) {
//                return onError(exchange, "권한 없음", HttpStatus.BAD_REQUEST);
//            }
            log.info("jwtTokenProvider.getUserId-end");

            ServerHttpRequest newRequest = request.mutate()
                    .header("user-id", subject)
                    .build();

            log.info("AuthorizationHeaderFilter End");
            return chain.filter(exchange.mutate().request(newRequest).build());
        };
    }

    //isJwtValid: JWT를 파싱하여 유효한 토큰인지 확인한다. 여기서 사용되는 token.secret는 다음 단계에서 입력하겠지만 유저 서비스에서 사용하는 토큰과 동일하다.

    // Mono(단일 값), Flux(다중 값) -> Spring WebFlux
    private Mono<Void> onError(ServerWebExchange exchange, Class<? extends Throwable> exceptionClass) {

        ServerHttpResponse response =  exchange.getResponse();

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

        } else if (exceptionClass == NoSuchFieldException.class ){
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            responseBody = new ErrorResponse<>(ErrorCode.NOT_FOUND_JWT_TOKEN);
        }
        // 성공 시
        else {
            exchange.getResponse().setStatusCode(exchange.getResponse().getStatusCode());
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            responseBody = SuccessResponse.create("AuthorizationHeaderFilter else");
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
