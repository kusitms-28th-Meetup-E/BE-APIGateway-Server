//package gwangjang.server.global.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.reactive.config.CorsRegistry;
//import org.springframework.web.reactive.config.EnableWebFlux;
//import org.springframework.web.reactive.config.WebFluxConfigurer;
//import org.springframework.web.reactive.config.WebFluxConfigurerComposite;
//
//@EnableWebFlux
//@Configuration
//public class WebFluxConfig {
//
//    @Bean
//    public WebFluxConfigurer corsConfigurer() {
//        return new WebFluxConfigurerComposite() {
//
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("http://localhost:5173")
//                        .allowedOrigins("*")
//                        .allowedMethods("*");
//            }
//        };
//    }
//}