package com.arya.SpringSecurityApp.service;

import com.arya.SpringSecurityApp.entity.OTP;
import com.arya.SpringSecurityApp.repository.OTPRepository;
import com.arya.SpringSecurityApp.repository.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

    private static final Logger logger = LoggerFactory.getLogger(OTPService.class);

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public void generateOTP(String username) {
        String otp = String.valueOf((int) (Math.random() * 9000) + 1000); // Generate a 4-digit OTP

        OTP otpEntity = null;
        otpEntity = otpRepository.findByUsername(username);
        if (otpEntity != null) {
            // Updating OTP, if existing username in otp table
            otpEntity.setOtp(otp);
            otpEntity.setGenerationTime(LocalDateTime.now());
            otpEntity.setValidUntil(LocalDateTime.now().plusMinutes(5));
        } else {
            // Creating a new Object of OTP
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
        return otpEntity != null && !otpEntity.getValidUntil().isBefore(LocalDateTime.now())
                && otpEntity.getOtp().equals(otp);
    }

    private void sendEmail(String username, String otp) {
        final String subject = "Password Forget Request";
        final String message = "<html><body><p>This OTP is sent regarding the reset of Password.<br/>" +
                "Valid only for 5 minutes from now.<br/><br/>" +
                "Your OTP: " + otp + "</p></body></html>";
        emailService.sendEmail(username, subject, message);
    }

}
