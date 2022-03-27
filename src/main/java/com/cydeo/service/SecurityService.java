package com.cydeo.service;


import com.cydeo.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public interface SecurityService extends UserDetailsService {

    public User loadUser(String value);

}
