package com.zerobase.zerostore.service;

import com.zerobase.zerostore.domain.User;
import com.zerobase.zerostore.dto.LoginRequest;
import com.zerobase.zerostore.dto.TokenResponse;
import com.zerobase.zerostore.dto.UserRequest;
import com.zerobase.zerostore.exception.CustomException;
import com.zerobase.zerostore.repository.UserRepository;
import com.zerobase.zerostore.security.JwtTokenProvider;
import com.zerobase.zerostore.type.Role;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.zerobase.zerostore.type.ErrorCode.*;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void registerUser(UserRequest userRequest) {
        if (userRepository.existsByPhoneNumber(userRequest.getPhoneNumber())) {
            throw new CustomException(USER_ALREADY_REGISTERED);
        }

        User user = User.builder()
                .name(userRequest.getName())
                .phoneNumber(userRequest.getPhoneNumber())
                .password(passwordEncoder.encode(userRequest.getPassword()))// 비밀번호 암호화
                .role(Role.USER)// 기본 역할 설정
                .build();
        userRepository.save(user);
    }

    @Transactional
    public void applyForPartner(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()->new CustomException(USER_NOT_FOUND));
        user.changeRole();
        userRepository.save(user);
    }

    public TokenResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByPhoneNumber(loginRequest.getPhoneNumber())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new CustomException(INVALID_PASSWORD);
        }

        return TokenResponse.builder()
                .token(jwtTokenProvider.generateToken(user.getPhoneNumber()))
                .build();
    }
}
