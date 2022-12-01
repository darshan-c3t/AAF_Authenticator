package com.c3t.authenticator.repository;

import com.c3t.authenticator.entity.Clients;
import com.c3t.authenticator.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByUserName(String username);
}
