package com.mon.akeengateway.error;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@ToString
public class ResponseError {
    private int status;
    private List<String> error;
    private String path;
    private String message;

    public static ResponseError buildForbiddenResponse(String message){
        return ResponseError.builder()
                .status(403)
                .message(message)
                .build();
    }
}
