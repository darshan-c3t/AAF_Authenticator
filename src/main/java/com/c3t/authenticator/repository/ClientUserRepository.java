package com.c3t.authenticator.repository;

import com.c3t.authenticator.entity.ClientUsers;
import com.c3t.authenticator.entity.Clients;
import com.c3t.authenticator.entity.Users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientUserRepository extends JpaRepository<ClientUsers, Long> {

    ClientUsers findByClientsAndUsers(Clients clients, Users users);
}
