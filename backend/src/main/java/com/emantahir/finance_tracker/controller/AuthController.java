package com.emantahir.finance_tracker.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.emantahir.finance_tracker.dto.LoginRequest;
import com.emantahir.finance_tracker.dto.LoginResponse;
import com.emantahir.finance_tracker.dto.RegisterRequest;
import com.emantahir.finance_tracker.model.Portfolio;
import com.emantahir.finance_tracker.model.User;
import com.emantahir.finance_tracker.repository.PortfolioRepository;
import com.emantahir.finance_tracker.repository.UserRepository;
import com.emantahir.finance_tracker.util.JwtService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final PortfolioRepository portfolioRepository;

    public AuthController(UserRepository userRepository, PortfolioRepository portfolioRepository,
            JwtService jwtService, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.portfolioRepository = portfolioRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Register new user
     * - prevents duplicate accounts using different credentials.
     * - makes login and user lookup consistent
     * default portoflio so we can
     * - ensure frontend doesnt crash when displaying dashboard
     * - users always start with at least one portfolio
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterRequest request) {

        if (userRepository.findByUsernameIgnoreCase(request.getUsername()).isPresent() ||
                userRepository.findByEmail(request.getEmail()).isPresent()) {
                    // BAD_REQUEST instead of CONFLICT: we want consistent 400-series responses.
            return new ResponseEntity<>("Username already exists", HttpStatus.BAD_REQUEST);
        }

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        // encoding passwords as they should never exist in database as plain text
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(newUser);

        Portfolio defaultPortfolio = new Portfolio();
        defaultPortfolio.setUser(savedUser);
        defaultPortfolio.setName("Primary Portfolio"); // front end expects this
        portfolioRepository.save(defaultPortfolio);

        return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
    }

    /**
     * Logs a user in and returns a JWT token.
     * - Centralised authentication handled by Spring Security.
     * - Ensures password comparison uses the configured encoder.
     * - Prevents token generation for invalid credentials.
     * - Frontend stores it for authenticated requests.
     */

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        //authentication delegated to spring security.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        // extract authenticated user details before token generation
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
         // JWT allows stateless sessions so no server-side session storage needed
        String token = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(new LoginResponse(token));
    }
}