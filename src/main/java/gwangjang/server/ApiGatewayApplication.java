package gwangjang.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import gwangjang.server.filter.AuthorizationHeaderFilter;
import gwangjang.server.handler.GlobalExceptionHandler;
import gwangjang.server.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;


@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "API Gateway", version = "1.0", description = "Gwang-Jang API Gateway v1.0"))
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @Bean
    public ErrorWebExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler(new ObjectMapper());
    }


    @Bean
    public KeyResolver tokenKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0));
    }

}
