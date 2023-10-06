package com.mon.akeengateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequireArgsConstructor
public class Router {

    @Value("${video.service.id}")
    private String videoServiceId;

    @Value("${video.service.path}")
    private String videoServicePath;

    @Value("${video.service.uri}")
    private String videoServiceUri;

    private final TokenValidationFilter tokenValidationFilter;

    @Bean
    RouteLocator customRouteLocator(RouteLocatorBuilder routeLocatorBuilder){
        return routeLocatorBuilder.routes()
                .route(videoServiceId,
                        predicateSpec -> predicateSpec
                                .path(videoServicePath)
                                .filters(customFilter -> customFilter.filter(tokenValidationFilter.apply(new TokenValidationFilter.Config())))
                                .uri(videoServiceUri)
                )
                .build();
    }
}
