package com.arya.SpringSecurityApp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

@Data
@Table(name = "users")
@Entity
public class User {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id;

    @NotNull
    private String username;

    @NotNull
    @Column(name = "password")
    private String Password;

    @NotNull
    private String name;

    @Column(name = "phonenumber")
    private String phoneNumber;

    private String address;

    @Column(name="password1")
    private String password_1;

    @Column(name = "password2")
    private String password_2;

}