package com.arya.SpringSecurityApp.service;

import com.arya.SpringSecurityApp.dao.UserRepo;
import com.arya.SpringSecurityApp.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
public class UserService {

    @Autowired
    private UserRepo repo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public UserService(UserRepo repo) {
        this.repo = repo;
    }

    public User saveUser(User user) {
        if (!isValidPassword(user.getPassword())) {
            throw new IllegalArgumentException("Password does not meet the criteria");
        }
        user.setPassword(encoder.encode(user.getPassword()));
        return repo.save(user);
    }

    private boolean isValidPassword(String password) {
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        return password.matches(regex);
    }

    public boolean existsByUsername(String username) {
        return repo.existsByUsername(username);
    }

    public List<String> getAllUsernames() {
        List<User> users = repo.findAll();
        
        // Filter out null users and null usernames
        return users.stream()
                .filter(Objects::nonNull) // Ensure no User object is null
                .map(User::getUsername)
                .filter(Objects::nonNull) // Ensure no username is null
                .collect(Collectors.toList());
    }

}
