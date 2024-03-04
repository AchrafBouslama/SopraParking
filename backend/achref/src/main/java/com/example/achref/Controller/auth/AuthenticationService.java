package com.example.achref.Controller.auth;

import com.example.achref.Entities.user.Role;
import com.example.achref.Entities.user.User;
import com.example.achref.Repositories.UserRepository;
import com.example.achref.config.JwtService;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Var;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    public  AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        System.out.println(jwtToken);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }


    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        System.out.println("Received authentication request: " + request);

        // Perform authentication
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            System.out.println("Authentication successful for user: " + authentication.getName());
        } catch (AuthenticationException e) {
            System.out.println("Authentication failed: " + e.getMessage());
            throw e; // Re-throw the exception to handle it further if needed
        }

        // Retrieve user details after successful authentication
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Generate JWT token
        String jwtToken = jwtService.generateToken(user);
        System.out.println("Generated JWT token: " + jwtToken);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
