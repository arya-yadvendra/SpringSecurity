package com.arya.SpringSecurityApp.service;

import com.arya.SpringSecurityApp.model.OTP;
import com.arya.SpringSecurityApp.model.User;
import com.arya.SpringSecurityApp.repository.OTPRepository;
import com.arya.SpringSecurityApp.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OTPService {

    @Autowired
    private OTPRepository otpRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepo userRepo;

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public void generateOTP(String username) {
        String otp = String.valueOf((int) (Math.random() * 9000) + 1000); // Generate a 4-digit OTP

        OTP otpEntity = null;
        otpEntity = otpRepository.findByUsername(username);
        if (otpEntity != null) {
            // Updating the existing OTP
            otpEntity.setOtp(otp);
            otpEntity.setGenerationTime(LocalDateTime.now());
            otpEntity.setValidUntil(LocalDateTime.now().plusMinutes(5));
        } else {
            // Creating a new OTP entry
            otpEntity = new OTP();
            otpEntity.setUsername(username);
            otpEntity.setOtp(otp);
            otpEntity.setGenerationTime(LocalDateTime.now());
            otpEntity.setValidUntil(LocalDateTime.now().plusMinutes(5)); // OTP valid for 5 minutes
        }

        otpRepository.save(otpEntity);

        sendEmail(username, otp);
    }

    public boolean validateOTP(String username, String otp) {
        OTP otpEntity = otpRepository.findByUsername(username);
        if (otpEntity == null || otpEntity.getValidUntil().isBefore(LocalDateTime.now())
                || !otpEntity.getOtp().equals(otp)) {
            if (otpEntity != null) {
                otpRepository.deleteByUsername(username);
            }
            return false;
        }
        return true;
    }
    // public boolean validateOTP(String username, String otp) {
    //     System.out.println("Username" + username);
    //     Optional<OTP> otpEntityOptional = Optional.ofNullable(otpRepository.findByUsername(username));
    //     System.out.println("OTP Entity:" + otpEntityOptional);
    //     if (otpEntityOptional.isEmpty()) {
    //         System.out.println("Inside false statement");
    //         return false;
    //     }

    //     OTP otpEntity = otpEntityOptional.get();
    //     if (otpEntity.getValidUntil().isBefore(LocalDateTime.now())) {
    //         //otpRepository.deleteByUsername(username);
    //         return false;
    //     }
    //     return true;
    // }

    private void sendEmail(String username, String otp) {
        final String subject = "Password Forget Request";
        final String message = "This OTP is sent regarding the reset of Password. " +
                "Valid only for 5 minutes from now. \nYour OTP: ";
        emailService.sendEmail(username, subject, message + otp);
    }

    public ResponseEntity<String> resetPassword(String username, String otp, String newPassword) {
        Optional<OTP> otpEntityOptional = Optional.ofNullable(otpRepository.findByUsernameAndOtp(username, otp));

        if (otpEntityOptional.isEmpty() || otpEntityOptional.get().getValidUntil().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired OTP");
        }

        Optional<User> optionalUser = Optional.ofNullable(userRepo.findByUsername(username));
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        if (!UserService.isValidPassword(newPassword)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password does not meet the criteria");
        }

        User user = optionalUser.get();
        user.setPassword(encoder.encode(newPassword));
        userRepo.save(user);
        otpRepository.deleteByUsername(username);
        return ResponseEntity.status(HttpStatus.OK).body("Password changed successfully!!");
    }
}
