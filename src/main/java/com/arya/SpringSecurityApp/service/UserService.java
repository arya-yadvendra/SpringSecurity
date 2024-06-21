package com.arya.SpringSecurityApp.service;

import com.arya.SpringSecurityApp.repository.UserRepo;
import com.arya.SpringSecurityApp.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class UserService {

    @Autowired
    private static UserRepo userRepo;

    @Autowired
    private static User user;

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public UserService(UserRepo repo) {
        UserService.userRepo = repo;
    }

    public User saveUser(User user) {
        if (isValidPassword(user.getPassword())) {
            throw new IllegalArgumentException("Password does not meet the criteria");
        }
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    public static boolean isValidPassword(String password) {
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        return !password.matches(regex);
    }

    public boolean existsByUsername(String username) {
        return userRepo.existsByUsername(username);
    }

    public List<String> getAllUsernames() {
        List<User> users = userRepo.findAll();
        
        // Filter out null users and null usernames
        return users.stream()
                .filter(Objects::nonNull) // Ensure no User object is null
                .map(User::getUsername)
                .filter(Objects::nonNull) // Ensure no username is null
                .collect(Collectors.toList());
    }
    public static ResponseEntity<?> changePassword(String username, String oldPassword, String newPassword) {
        Optional<User> optionalUser = Optional.ofNullable(userRepo.findByUsername(username));
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User Not Found");
        }
        User user = optionalUser.get();
        if (!encoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }
        if (isValidPassword(newPassword)) {
            throw new IllegalArgumentException("New password does not meet the criteria");
        }
        user.setPassword(encoder.encode(newPassword));
        userRepo.save(user);
        return ResponseEntity.status(HttpStatus.OK).body("Password changed successfully!!");
    }

    public User findByUsername(String email) {
        return userRepo.findByUsername(email);
    }

    public static void resetPassword(String username, String newPassword) {
        Optional<User> optionalUser = Optional.ofNullable(userRepo.findByUsername(username));
        if (optionalUser.isEmpty()) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User Not Found");
            return;
        }
        User user = optionalUser.get();  // Retrieve the User object from the Optional
        user.setPassword(encoder.encode(newPassword));
        userRepo.save(user);
        ResponseEntity.status(HttpStatus.OK).body("Password changed successfully!!");
    }

}
