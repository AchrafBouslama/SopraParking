package com.example.achref.Controller.auth;

import com.example.achref.Entities.user.PasswordResetToken;
import com.example.achref.Entities.user.User;
import com.example.achref.Repositories.PasswordResetTokenRepository;
import com.example.achref.Repositories.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/api/vi/auth")
@RequiredArgsConstructor
public class AutheticationController {

    private final AuthenticationService authenticationService;
    private final HttpServletRequest servletRequest;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder; // Ajoutez cette ligne pour injecter le PasswordEncoder




    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request){
        System.out.println("email"+request.getEmail());

        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
        return ResponseEntity.ok(authenticationService.authenticate(request));


    }


    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        if (email != null) {
            Optional<User> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                String resetToken = UUID.randomUUID().toString();
                authenticationService.createPasswordResetTokenForUser(user, resetToken);
                // Envoyez le token de réinitialisation de mot de passe par e-mail à l'utilisateur
                String resetLink = "http://localhost:8080/api/vi/auth/reset-password?token=" + resetToken;
                emailService.sendPasswordResetEmail(email, resetLink);

                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.badRequest().build(); // Requête incorrecte si l'e-mail est manquant
        }
    }


    @PostMapping("/set-new-password")
    public ResponseEntity<?> setNewPassword(@RequestParam("token") String token,
                                            @RequestBody String newPassword) {
        // Valider le token de réinitialisation de mot de passe
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token);
        if (resetToken == null || resetToken.isExpired()) {
            return ResponseEntity.badRequest().build();
        }

        // Mettre à jour le mot de passe de l'utilisateur
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        // Supprimer le token de réinitialisation de mot de passe
        passwordResetTokenRepository.delete(resetToken);

        return ResponseEntity.ok().build();
    }





}

