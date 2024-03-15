package com.example.achref.Services;

import com.example.achref.Entities.user.User;
import com.example.achref.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserProfileService {

    @Autowired

    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserProfileService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }


    // Créer un profil utilisateur
    public void createUserProfile(User user) {
        // Encode le mot de passe avant de le sauvegarder
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        userRepository.save(user);
    }

    public List<User> displayUser() {
        return userRepository.findAll();
    }

    public void deleteUser(Integer idUser) {
        userRepository.deleteById(idUser);
    }

    public void updateUser(User user) {
            // Vérifiez si l'utilisateur existe dans la base de données
            User existingUser = userRepository.findById(user.getIduserr()).orElse(null);
            if (existingUser == null) {

            } else {
                existingUser.setEmail(user.getEmail());
                existingUser.setFirstname(user.getFirstname());
                existingUser.setLastname(user.getLastname());
                existingUser.setRole(user.getRole());
                String hashedPassword = passwordEncoder.encode(user.getPassword());
                existingUser.setPassword(hashedPassword);


                // Enregistrez les modifications dans la base de données
                userRepository.save(existingUser);
            }
    }





}
