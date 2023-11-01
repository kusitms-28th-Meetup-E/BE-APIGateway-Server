//package gwangjang.server.global.config;
//
//import gwangjang.server.global.filter.AuthorizationHeaderFilter;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.gateway.filter.GatewayFilter;
//import org.springframework.cloud.gateway.route.RouteLocator;
//import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class FilterConfig {
//
//    @Bean
//    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder){
//
//        AuthorizationHeaderFilter authorizationHeaderFilter = new AuthorizationHeaderFilter();
//
//        return builder.routes()
//                .route("member auth",r -> r
//                        .path("/member/auth/**")  //라우터 등록
//                        .and()
//                        .method("POST","GET")
//                        .filters(f -> f
//                                .rewritePath("/member/(?<segment>.*)", "/${segment}")
//                        )
//                        .uri("lb://MEMBERSERVICE")
//
//                ).route("member" , r -> r
//                        .path("/member/**")
//                        .and()
//                        .method("POST","GET")
//                        .filters(f -> f
//                                .rewritePath("/member/(?<segment>.*)", "/${segment}")
////                                .filter(new AuthorizationHeaderFilter().apply(new AuthorizationHeaderFilter.Config())))
//                                .filter(authorizationHeaderFilter.apply(new AuthorizationHeaderFilter.Config())))
//
//                        .uri("lb://MEMBERSERVICE")
//                )
//                .build();
//    }
//}