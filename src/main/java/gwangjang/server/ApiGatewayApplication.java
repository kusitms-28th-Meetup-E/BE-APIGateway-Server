package gwangjang.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import gwangjang.server.global.exception.GlobalExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.CrossOrigin;
import reactor.core.publisher.Mono;

@CrossOrigin("*")
@SpringBootApplication
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
