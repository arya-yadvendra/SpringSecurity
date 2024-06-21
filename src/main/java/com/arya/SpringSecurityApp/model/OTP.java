package com.arya.SpringSecurityApp.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name="otp")
public class OTP {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String otp;
    private LocalDateTime generationTime;
    private LocalDateTime validUntil;
}
