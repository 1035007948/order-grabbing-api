package com.doubao.ordergrabbing.service;

import com.doubao.ordergrabbing.dto.JwtResponse;
import com.doubao.ordergrabbing.dto.LoginRequest;
import com.doubao.ordergrabbing.dto.RegisterRequest;
import com.doubao.ordergrabbing.entity.User;
import com.doubao.ordergrabbing.exception.BusinessException;
import com.doubao.ordergrabbing.repository.UserRepository;
import com.doubao.ordergrabbing.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        user.setRoles(roles);

        userRepository.save(user);
    }

    public JwtResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("用户名或密码错误"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        String token = jwtTokenProvider.generateToken(user.getUsername(), user.getRoles());
        return new JwtResponse(token, user.getUsername());
    }
}
