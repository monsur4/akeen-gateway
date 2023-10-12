package com.mon.akeengateway.validation.impl;

@Service
@Slf4j
public class TokenValidationServiceImpl implements TokenValidationService {

    @Override
    public boolean isValidToken(String token){
        ValidateTokenRequest request = ValidateTokenRequest.builder()
                .setToken(token)
                .build();

        log.info("calling authentication service with validateTokenRequest: [{}]", request);

        // TODO: MAKE CALL TO AUTHENTICATION SERVICE

        // create a response stub
        ValidateTokenResponse validateTokenResponse = new ValidateTokenResponse();
        log.info(validateTokenResponse.toString());

        return validateTokenResponse.getIsValid();
    }
}
