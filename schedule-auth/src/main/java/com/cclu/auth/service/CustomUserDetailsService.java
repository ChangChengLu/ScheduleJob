package com.cclu.auth.service;

import com.cclu.auth.model.LoginUser;
import com.cclu.auth.model.UserCredential;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    @Resource
    private AuthService authService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserCredential credential = authService.findUserByUsername(username);
        if(credential == null){
            log.error("user not found");
            return null;
        }
        return new LoginUser(credential);
    }
}

