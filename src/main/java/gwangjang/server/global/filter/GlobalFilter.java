package gwangjang.server.global.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {

    public GlobalFilter(){
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest(); // reactive포함된거로 import
            ServerHttpResponse response = exchange.getResponse();

            log.info("Global com.example.scg.filter baseMessgae: {}", config.getBaseMessage());

            // Global pre Filter
            if (config.isPreLogger()){
                log.info("Global Filter Start: request id -> {}" , request.getId());
                log.info("Global Filter Start: request path -> {}" , request.getPath());
            }

            // 예제: /token/** 경로에 대한 요청일 때만 실행
            if (request.getPath().toString().startsWith("/token/")) {
                log.info("Global com.example.scg.filter baseMessgae: {}", config.getBaseMessage());
                // Your global filter logic here

                //Mono는 webflux에서 단일값 전송할때 Mono값으로 전송
                return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                    log.info("Global Filter End: response status code -> {}" , response.getStatusCode());
                }));
            } else {
                // /token/** 경로가 아니면 그냥 다음 필터로 이동
                return chain.filter(exchange);
            }


        };
    }

    @Data
    public static class Config {
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
    }
}
