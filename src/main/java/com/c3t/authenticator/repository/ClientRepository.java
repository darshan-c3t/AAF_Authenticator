package com.c3t.authenticator.repository;

import com.c3t.authenticator.entity.Clients;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Clients, Long> {

    Optional<Clients> findByUserName(String userName);
}
