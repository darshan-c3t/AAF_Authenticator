package com.c3t.authenticator.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
@Builder
public class TokenUserResponse {
    private Long clientId;
    private Long userId;
    private Map<String, String > customPropeties;

}
