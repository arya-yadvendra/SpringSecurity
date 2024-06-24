package com.arya.SpringSecurityApp.repository;

import com.arya.SpringSecurityApp.entity.OTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OTPRepository extends JpaRepository<OTP, String> {

    OTP findByUsername(String username);
}
