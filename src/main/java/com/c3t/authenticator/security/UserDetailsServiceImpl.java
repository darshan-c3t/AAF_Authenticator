package com.c3t.authenticator.security;

import com.c3t.authenticator.entity.Clients;
import com.c3t.authenticator.repository.ClientRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    ClientRepository clientRepository;

    @Value("${client.super.username}")
    private String superUsername;

    @Value("${client.super.password}")
    private String superPassword;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Username -> " + username );
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        if(StringUtils.equalsAnyIgnoreCase(username, superUsername)) {
            return User.withUsername(superUsername).password(passwordEncoder.encode(superPassword)).authorities("USER").build();
        }
        Optional<Clients> client = clientRepository.findByUserName(username);
        if(!client.isPresent()) {
            throw new UsernameNotFoundException(username);
        }
        return User.withUsername(client.get().getUserName()).password(client.get().getPassword()).authorities("USER").build();
    }
}
