package com.reservas.ms_auth.service;

import com.reservas.ms_auth.config.JwtService;
import com.reservas.ms_auth.dto.LoginRequest;
import com.reservas.ms_auth.dto.LoginResponse;
import com.reservas.ms_auth.dto.RegisterRequest;
import com.reservas.ms_auth.dto.RegisterResponse;
import com.reservas.ms_auth.exception.EmailAlreadyExistsException;
import com.reservas.ms_auth.exception.InvalidCredentialsException;
import com.reservas.ms_auth.model.User;
import com.reservas.ms_auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRol("USER");
        user.setFechaCreacion(LocalDateTime.now());

        User saved = userRepository.save(user);

        return new RegisterResponse(saved.getId(), saved.getEmail(), saved.getRol());
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRol(), user.getId());

        return new LoginResponse(token, "Bearer", 86400000L, user.getEmail(), user.getRol());
    }
}