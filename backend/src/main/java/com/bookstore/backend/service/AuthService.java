package com.bookstore.backend.service;
import org.springframework.stereotype.Service;
import java.util.Map;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.bookstore.backend.dto.LoginRequest;
import com.bookstore.backend.dto.LoginResponse;
import com.bookstore.backend.entities.User;
import com.bookstore.backend.repository.UserRepository;
import com.bookstore.backend.utils.JwtUtils;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils; 

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    // đăng nhập
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email or password is incorrect"));

       if (user.getStatus() == 0) {
        throw new IllegalArgumentException("Account has been blocked");
       }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Email or password is incorrect");
        }

        // Tạo token
        String token = jwtUtils.generateToken(user);

        return LoginResponse.builder()
                .token(token)
                .id(user.getId())
                .email(user.getEmail())
                .fullname(user.getFullname())
                .role(user.getRole())
                .build();
    }

    // lấy thông tin tài khoản từ token
        public Map<String, Object> getUserFromToken(String token) {
        return jwtUtils.getUserFromToken(token);
    }
}

