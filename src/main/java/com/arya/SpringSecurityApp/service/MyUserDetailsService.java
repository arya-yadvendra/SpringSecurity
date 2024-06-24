package com.arya.SpringSecurityApp.service;

import com.arya.SpringSecurityApp.repository.UserRepo;
import com.arya.SpringSecurityApp.entity.User;
import com.arya.SpringSecurityApp.entity.UserPrincipal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user= userRepo.findByUsername(username);

        if (user==null) {
            System.out.println("User not found!");
            throw new UsernameNotFoundException("User not found");
        }
        return new UserPrincipal(user);
    }

}
