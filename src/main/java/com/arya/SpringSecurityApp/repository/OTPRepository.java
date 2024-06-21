package com.arya.SpringSecurityApp.repository;

import com.arya.SpringSecurityApp.model.OTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OTPRepository extends JpaRepository<OTP, String> {

    OTP findByUsernameAndOtp(String username, String otp);
    void deleteByUsername(String username);
    OTP findByUsername(String username);
}
