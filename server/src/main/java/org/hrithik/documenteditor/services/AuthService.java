package org.hrithik.documenteditor.services;

import org.hrithik.documenteditor.repositories.UserRepository;
import org.hrithik.documenteditor.schemas.UserSchema;
import org.hrithik.documenteditor.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public String signup(String username, String password) {
        if (userRepository.findByUsername(username).isPresent())
            throw new RuntimeException("UserSchema already exists");

        UserSchema user = new UserSchema (username, passwordEncoder.encode(password));
        userRepository.save(user);
        return jwtTokenProvider.generateToken(username);
    }

    public String login(String username, String password) {
        UserSchema user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("UserSchema not found"));

        if (!passwordEncoder.matches(password, user.getPassword()))
            throw new RuntimeException("Invalid password");

        return jwtTokenProvider.generateToken(username);
    }
}
