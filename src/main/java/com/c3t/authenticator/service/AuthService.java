package com.c3t.authenticator.service;

import com.c3t.authenticator.dto.*;
import com.c3t.authenticator.entity.ClientUsers;
import com.c3t.authenticator.entity.Clients;
import com.c3t.authenticator.entity.Users;
import com.c3t.authenticator.exceptions.BadRequestException;
import com.c3t.authenticator.repository.ClientRepository;
import com.c3t.authenticator.repository.ClientUserRepository;
import com.c3t.authenticator.repository.UserRepository;
import com.c3t.authenticator.utils.ApplicationUtils;
import com.c3t.authenticator.utils.HashingUtil;
import com.c3t.authenticator.utils.JwtTokenUtil;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ClientUserRepository clientUserRepository;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    public TokenResponse loginUser(LoginDto loginDto, String clientUserName) throws Exception {
        Optional<Clients> existingClient = clientRepository.findByUserName(clientUserName);
        if(!existingClient.isPresent()) {
            throw new BadRequestException("User not found for client");
        }
        Optional<Users> existingUser =  userRepository.findByUserName(loginDto.getUsername());
        if(!existingUser.isPresent()) {
            throw new BadRequestException("User not found");
        }
        String loginPassword = HashingUtil.convertToMd5HashString(loginDto.getPassword());
        if(!StringUtils.equalsAnyIgnoreCase(existingUser.get().getPassword(), loginPassword)) {
            throw new BadRequestException("Incorrect username and password");
        }

        ClientUsers users = clientUserRepository.findByClientsAndUsers(existingClient.get(), existingUser.get());
        if(Objects.isNull(users)) {
            throw new BadRequestException("User not associated to Client");
        }
        String tokenUserName = existingUser.get().getUserName() + "_" + existingClient.get().getUserName();
        String token = jwtTokenUtil.generateToken(tokenUserName);
        ApplicationUtils.tokenRecords.put(tokenUserName, users);
        return TokenResponse.builder().accessToken(token).userId(existingUser.get().getId()).clientId(existingClient.get().getId())
                .expiresIn(jwtTokenUtil.getExpirationDateFromToken(token)).createdDate(jwtTokenUtil.getCreatedDateFromToken(token))
                .tokenType("Bearer").build();
    }

    public ControllerResponse registerClient(ClientDto clientDto) throws Exception {
        Optional<Clients> existingClient = clientRepository.findByUserName(clientDto.getUsername());
        if(existingClient.isPresent()) {
            throw new BadRequestException("Already present Client");
        }
        Clients clients = Clients.builder().clientName(clientDto.getClientName()).userName(clientDto.getUsername()).password(HashingUtil.convertBcryptString(clientDto.getPassword())).build();
        clientRepository.saveAndFlush(clients);
        return ControllerResponse.builder().clientUserName(clients.getUserName()).clientId(clients.getId()).build();
    }

    public ControllerResponse registerUser(UserDto userDto, String clientUserName) throws Exception {
        Optional<Clients> existingClient = clientRepository.findByUserName(clientUserName);
        if(!existingClient.isPresent()) {
            throw new BadRequestException("Client not found");
        }
        Optional<Users> existingUser =  userRepository.findByUserName(userDto.getUsername());
        Users users = null;
        String jsonProp = "";
        if(Objects.nonNull(userDto.getProperties())) {
            jsonProp = new Gson().toJson(userDto.getProperties());
        }
        if(!existingUser.isPresent()) {
            users = Users.builder().userName(userDto.getUsername()).password(HashingUtil.convertToMd5HashString(userDto.getPassword())).build();
            userRepository.saveAndFlush(users);
            ClientUsers clientUsers = ClientUsers.builder().users(users).clients(existingClient.get()).customProperties(jsonProp).build();
            clientUserRepository.saveAndFlush(clientUsers);
        } else {
            users = existingUser.get();

            ClientUsers existingClientUsers = clientUserRepository.findByClientsAndUsers(existingClient.get(), users);
            if(Objects.nonNull(existingClientUsers)) {
                existingClientUsers.setCustomProperties(jsonProp);
                clientUserRepository.saveAndFlush(existingClientUsers);
            } else {
                ClientUsers clientUsers = ClientUsers.builder().users(users).clients(existingClient.get()).customProperties(jsonProp).build();
                clientUserRepository.saveAndFlush(clientUsers);
            }
        }
        return ControllerResponse.builder().clientUserName(users.getUserName()).clientId(users.getId()).build();
    }

    public TokenUserResponse verifyToken(String token, String clientUserName) throws Exception {
        if(StringUtils.isBlank(token)) {
            throw new BadRequestException("Token empty");

        }
        boolean expired = jwtTokenUtil.isTokenExpired(token);
        String userName = jwtTokenUtil.getUsernameFromToken(token);
        if(expired) {
            ApplicationUtils.tokenRecords.remove(userName);
            throw new BadRequestException("Token expired");

        }
        if(ApplicationUtils.blackListToken.indexOf(token) >= 0) {
            ApplicationUtils.tokenRecords.remove(userName);
            throw new BadRequestException("Token expired");
        }
        ClientUsers users = ApplicationUtils.tokenRecords.get(userName);
        if(Objects.isNull(users)) {
            throw new BadRequestException("Invalid Token");
        }
        Map<String, String> customProperties = new HashMap<>();
        if(StringUtils.isNotBlank(users.getCustomProperties())) {
            customProperties = new Gson().fromJson(users.getCustomProperties(), Map.class);
        }

        return TokenUserResponse.builder().userId(users.getUsers().getId()).clientId(users.getClients().getId())
                .customPropeties(customProperties).build();
    }

    public Boolean invalidateToken(String token, String clientUserName) throws Exception {
        if(StringUtils.isBlank(token)) {
            throw new BadRequestException("Token empty");
        }
        boolean expired = jwtTokenUtil.isTokenExpired(token);
        String userName = jwtTokenUtil.getUsernameFromToken(token);
        if(expired) {
            ApplicationUtils.tokenRecords.remove(userName);
            throw new BadRequestException("Token expired");

        }
        ApplicationUtils.blackListToken.add(token);
        return true;
    }
}
