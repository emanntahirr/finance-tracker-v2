package com.emantahir.finance_tracker.repository; // Adjust package name as necessary

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.emantahir.finance_tracker.model.User;


public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsernameIgnoreCase(String username);
    Optional<User> findByEmail(String email);
}