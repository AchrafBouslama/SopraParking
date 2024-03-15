package com.example.achref.Services;

import com.example.achref.Controller.auth.AuthenticationRequest;
import com.example.achref.Controller.auth.AuthenticationResponse;
import com.example.achref.Controller.auth.EmailService;
import com.example.achref.Controller.auth.RegisterRequest;
import com.example.achref.Entities.user.PasswordResetToken;
import com.example.achref.Entities.user.Role;
import com.example.achref.Entities.user.User;
import com.example.achref.Repositories.PasswordResetTokenRepository;
import com.example.achref.Repositories.UserRepository;
import com.example.achref.config.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor

public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;



    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserDetailsService userDetailsService;




    public void createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setUser(user);
        passwordResetToken.setToken(token);
        passwordResetToken.setExpiryDate(LocalDateTime.now().plusHours(1)); // par exemple, expire dans 1 heure
        passwordResetTokenRepository.save(passwordResetToken);
    }

    /*  public  AuthenticationResponse register(RegisterRequest request) {
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

*/
  public AuthenticationResponse register(RegisterRequest request) {
      String generatedPassword = generateRandomPassword();
      User user = User.builder()
              .firstname(request.getFirstname())
              .lastname(request.getLastname())
              .email(request.getEmail())
              .password(passwordEncoder.encode(generatedPassword)) // Encodez le mot de passe généré
              .role(Role.USER)
              .build();
      userRepository.save(user);



      // Envoyez le mot de passe généré par e-mail
      emailService.sendPasswordByEmail(request.getEmail(), generatedPassword);

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

        // Generate JWT token
        String jwtToken = jwtService.generateToken((UserDetails) authentication.getPrincipal());
        System.out.println("Generated JWT token: " + jwtToken);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }


    private String generateRandomPassword() {
        int length = 10; // Longueur du mot de passe
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+"; // Caractères autorisés dans le mot de passe
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }







}
