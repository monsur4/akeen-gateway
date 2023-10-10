package com.mon.akeengateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mon.akeengateway.error.ResponseError;
import com.mon.akeengateway.validation.TokenValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class TokenValidationFilter implements GatewayFilter, Ordered {

    @Autowired
    private final TokenValidationService tokenValidationService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = extractToken(exchange.getRequest().getHeaders());

        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        try {
            if (!StringUtils.hasText(token)) {
                response.setStatusCode(HttpStatus.FORBIDDEN);
                DataBuffer dataBuffer = response.bufferFactory()
                        .wrap(new ObjectMapper().writeValueAsBytes(ResponseError.buildForbiddenResponse("Provide a bearer token")));
                return response.writeWith(Mono.just(dataBuffer));
            }

            boolean isValidToken = tokenValidationService.isValidToken(token);

            if (isValidToken) {
                return chain.filter(exchange);
            } else {
                // Token is invalid; return a 403 Forbidden response
                response.setStatusCode(HttpStatus.FORBIDDEN);
                DataBuffer dataBuffer = response.bufferFactory()
                        .wrap(new ObjectMapper().writeValueAsBytes(ResponseError.buildForbiddenResponse("Invalid token")));
                return response.writeWith(Mono.just(dataBuffer));
            }
        }catch (JsonProcessingException e){
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return response.writeWith(Mono.empty());
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private String extractToken(HttpHeaders httpHeaders){
        String authorizationToken = httpHeaders.getFirst(HttpHeaders.AUTHORIZATION);
        if(StringUtils.hasText(authorizationToken)){
            if (authorizationToken.startsWith("Bearer ")) {
                // Extract the token part
                return authorizationToken.substring(7);
            }
        }
        return null;
    }
}
