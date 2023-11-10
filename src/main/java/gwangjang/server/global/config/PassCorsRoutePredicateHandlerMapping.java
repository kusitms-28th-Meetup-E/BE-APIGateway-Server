//package gwangjang.server.global.config;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.cloud.gateway.config.GlobalCorsProperties;
//import org.springframework.cloud.gateway.handler.FilteringWebHandler;
//import org.springframework.cloud.gateway.handler.RoutePredicateHandlerMapping;
//import org.springframework.cloud.gateway.route.RouteLocator;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Primary;
//import org.springframework.core.env.Environment;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//public class PassCorsRoutePredicateHandlerMapping extends RoutePredicateHandlerMapping {
//
//    private static final Logger logger = LoggerFactory.getLogger(PassCorsRoutePredicateHandlerMapping.class);
//
//    public PassCorsRoutePredicateHandlerMapping(FilteringWebHandler webHandler, RouteLocator routeLocator,
//                                                GlobalCorsProperties globalCorsProperties, Environment environment) {
//        super(webHandler, routeLocator, globalCorsProperties, environment);
//    }
//
//    @Override
//    public Mono<Object> getHandler(ServerWebExchange exchange) {
//        logger.info("[PassCorsRoutePredicateHandlerMapping] getHandler");
//        return getHandlerInternal(exchange).map(handler -> {
//            logger.info(exchange.getLogPrefix() + "Mapped to " + handler);
//
//            // CORS 체크 로직 제거
//
//
//            return handler;
//        });
//    }
//
//    @Bean
//    @Primary
//    public RoutePredicateHandlerMapping passCorsRoutePredicateHandlerMapping(
//            FilteringWebHandler webHandler, RouteLocator routeLocator,
//            GlobalCorsProperties globalCorsProperties, Environment environment) {
//        return new PassCorsRoutePredicateHandlerMapping(webHandler, routeLocator,
//                globalCorsProperties, environment);
//    }
//}
