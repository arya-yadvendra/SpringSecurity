package com.arya.SpringSecurityApp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    private String password;

    @NotNull
    private String name;


    private String phone_no;


    private String address;

}