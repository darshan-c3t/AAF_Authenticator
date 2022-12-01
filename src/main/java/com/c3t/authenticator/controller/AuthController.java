package com.c3t.authenticator.controller;

import com.c3t.authenticator.dto.*;
import com.c3t.authenticator.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping(value ="/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public RestResponse getLoginDetails (@RequestBody LoginDto loginDto, Principal principal) throws Exception {
        TokenResponse response =  authService.loginUser(loginDto, principal.getName());
        return RestResponse.builder().success(RestResponse.RESPONSE_SUCCESS).data(response).build();
    }

    @RequestMapping(value = "/client", method = RequestMethod.POST)
    public RestResponse registerClient (@RequestBody ClientDto clients) throws  Exception{
        ControllerResponse response = authService.registerClient(clients);
        return RestResponse.builder().success(RestResponse.RESPONSE_SUCCESS).data(response).build();
    }

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public RestResponse registerUser (@RequestBody UserDto users, Principal principal) throws  Exception{
        ControllerResponse response = authService.registerUser(users, principal.getName());
        return RestResponse.builder().success(RestResponse.RESPONSE_SUCCESS).data(response).build();
    }

    @RequestMapping(value = "/verify", method = RequestMethod.POST)
    public RestResponse verifyToken (@RequestBody TokenVerifyRequest tokenRequest, Principal principal) throws  Exception{
        TokenUserResponse response = authService.verifyToken(tokenRequest.getToken(), principal.getName());
        return RestResponse.builder().success(RestResponse.RESPONSE_SUCCESS).data(response).build();
    }

    @RequestMapping(value = "/invalidate", method = RequestMethod.POST)
    public RestResponse invalidateToken (@RequestBody TokenVerifyRequest tokenRequest, Principal principal) throws  Exception{
        boolean data =  authService.invalidateToken(tokenRequest.getToken(), principal.getName());
        return RestResponse.builder().success(RestResponse.RESPONSE_SUCCESS).data(data).build();
    }
}
