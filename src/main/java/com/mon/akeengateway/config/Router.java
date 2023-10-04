package com.mon.akeengateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Router {

    @Value("${video.service.id}")
    private String videoServiceId;

    @Value("${video.service.path}")
    private String videoServicePath;

    @Value("${video.service.uri}")
    private String videoServiceUri;

    @Bean
    RouteLocator customRouteLocator(RouteLocatorBuilder routeLocatorBuilder){
        return routeLocatorBuilder.routes()
                .route(videoServiceId,
                        predicateSpec -> predicateSpec
                                .path(videoServicePath)
                                .filters(gatewayFilterSpec -> gatewayFilterSpec
                                        .addRequestHeader("X-Tenant","mon")
                                        .addResponseHeader("X-Genre", "thriller"))
                                .uri(videoServiceUri)
                )
                .build();
    }
}
