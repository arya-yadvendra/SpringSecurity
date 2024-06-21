package com.arya.SpringSecurityApp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;
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
    private String name;

    @NotNull
    private String address;

    @NotNull
    private String password;

    @NotNull
    private String phone_no;
}