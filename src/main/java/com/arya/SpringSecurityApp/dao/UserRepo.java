package com.arya.SpringSecurityApp.dao;

import com.arya.SpringSecurityApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserRepo extends JpaRepository<User, Integer> {

    User findByUsername(String username);
    boolean existsByUsername(String username);

}
