package com.arya.SpringSecurityApp.repository;

import com.arya.SpringSecurityApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepo extends JpaRepository<User, Long> {

    User findByUsername(String username);
    boolean existsByUsername(String username);
}
