package com.c3t.authenticator.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class TokenResponse {
    private String accessToken;
    private Long clientId;
    private Long userId;
    private Date createdDate;
    private Date expiresIn;
    private String tokenType;

}
