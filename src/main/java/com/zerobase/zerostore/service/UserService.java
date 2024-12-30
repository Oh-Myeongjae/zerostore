package com.zerobase.zerostore.service;

import com.zerobase.zerostore.domain.User;
import com.zerobase.zerostore.dto.LoginRequestDto;
import com.zerobase.zerostore.dto.TokenResponseDto;
import com.zerobase.zerostore.dto.UserRequestDto;
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
    public void registerUser(UserRequestDto userRequestDto) {
        if (userRepository.existsByPhoneNumber(userRequestDto.getPhoneNumber())) {
            throw new CustomException(USER_ALREADY_REGISTERED);
        }

        User user = User.builder()
                .name(userRequestDto.getName())
                .phoneNumber(userRequestDto.getPhoneNumber())
                .password(passwordEncoder.encode(userRequestDto.getPassword()))// 비밀번호 암호화
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

    public TokenResponseDto login(LoginRequestDto loginRequestDto) {
        User user = userRepository.findByPhoneNumber(loginRequestDto.getPhoneNumber())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new CustomException(INVALID_PASSWORD);
        }

        return TokenResponseDto.builder()
                .token(jwtTokenProvider.generateToken(user.getPhoneNumber()))
                .build();
    }
}
