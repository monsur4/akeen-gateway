package com.mon.akeengateway.filter;

import com.mon.akeengateway.validation.TokenValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Component
public class TokenValidationFilter extends AbstractGatewayFilterFactory<TokenValidationFilter.Config> {

    @Autowired
    private final TokenValidationService tokenValidationService;

    public TokenValidationFilter(TokenValidationService tokenValidationService) {
        super(Config.class);
        this.tokenValidationService = tokenValidationService;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String token = extractToken(exchange.getRequest().getHeaders());

            if(!StringUtils.hasText(token)){
                return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Provide a bearer token"));
            }

            boolean isValidToken = tokenValidationService.isValidToken(token);

            if (isValidToken){
                return chain.filter(exchange);
            }else {
                // Token is invalid; return a 403 Forbidden response
                return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token"));
            }
        };
    }

    private String extractToken(HttpHeaders httpHeaders){
        String authorizationToken = httpHeaders.getFirst("Authorization");
        if(StringUtils.hasText(authorizationToken)){
            if (authorizationToken.startsWith("Bearer ")) {
                // Extract the token part
                return authorizationToken.substring(7);
            }
        }
        return null;
    }

    public static class Config{

    }
}
