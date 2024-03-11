package com.cclu.auth.service;

import com.cclu.auth.model.UserCredential;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AuthService {

    @Resource
    private PasswordEncoder passwordEncoder;

    public UserCredential findUserByUsername(String username){
        // 这里模拟一个测试的，用户账号信息
        UserCredential userCredential = new UserCredential();
        userCredential.setUsername(username);
        userCredential.setPassword(passwordEncoder.encode("123456"));
        userCredential.setUserId("2023001");

        return userCredential;
    }
}
