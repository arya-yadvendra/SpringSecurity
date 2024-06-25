package com.arya.SpringSecurityApp.service;

import com.arya.SpringSecurityApp.exception.InvalidPasswordException;
import com.arya.SpringSecurityApp.exception.InvalidPhoneNoException;
import com.arya.SpringSecurityApp.exception.InvalidUsernameException;
import com.arya.SpringSecurityApp.repository.UserRepo;
import com.arya.SpringSecurityApp.entity.User;
import com.arya.SpringSecurityApp.response.GenericResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.InvalidNameException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Service
public class UserService {

    @Autowired
    private static UserRepo userRepo;


    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public UserService(UserRepo repo) {
        UserService.userRepo = repo;
    }

    public User saveUser(User user) throws InvalidNameException {
        if (!isValidEmail(user.getUsername())) {
            throw new InvalidUsernameException("Invalid username(email) format");
        }
        if (isValidPassword(user.getPassword())) {
            throw new InvalidPasswordException("Password does not meet the criteria");
        }
        if (!isValidPhoneNo(user.getPhone_no())){
            throw new InvalidPhoneNoException("Phone number is invalid");
        }

        user.setPassword(encoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    public static boolean isValidPassword(String password) {
        String regexPass = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        return !password.matches(regexPass);
    }
    public static boolean isValidPhoneNo(String phoneNo){
        if(phoneNo==null){
            return true;
        }
        String regexPhone = "^[0-9]{10,12}$";
        Pattern pattern = Pattern.compile(regexPhone);
        Matcher matcher = pattern.matcher(phoneNo);
        return matcher.matches();
    }
    public boolean existsByUsername(String username) {
        return userRepo.existsByUsername(username);
    }

    public List<String> getAllUsernames() {
        List<User> users = userRepo.findAll();

        return users.stream()
                .filter(Objects::nonNull) // Ensure no User object is null
                .map(User::getUsername)
                .filter(Objects::nonNull) // Ensure no username is null
                .collect(Collectors.toList());
    }

    public static ResponseEntity<GenericResponse<String>> changePassword(String username, String oldPassword, String newPassword) {
        GenericResponse<String> response = new GenericResponse<>();
        Optional<User> optionalUser = Optional.ofNullable(userRepo.findByUsername(username));

        if (optionalUser.isEmpty()) {
            response.setStatus("failure");
            response.setMessage("User doesn't exist!");
        }
        else {
            if(oldPassword == null || newPassword==null){
                response.setData("failure");
                response.setMessage("Old or New Password can't be null!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            User user = optionalUser.get();
            if (!encoder.matches(oldPassword, user.getPassword())) {

                response.setStatus("failure");
                response.setMessage("Old Password is incorrect!");
            }
            else if (isValidPassword(newPassword)) {
                response.setStatus("failure");
                response.setMessage("New password does not meet the criteria");
            }
            else {
                user.setPassword(encoder.encode(newPassword));
                userRepo.save(user);
                response.setStatus("success");
                response.setMessage("Password changed successfully!!");
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
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
        User user = optionalUser.get();
        user.setPassword(encoder.encode(newPassword));
        userRepo.save(user);
        ResponseEntity.status(HttpStatus.OK).body("Password changed successfully!!");
    }
}
