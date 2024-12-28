package com.zerobase.zerostore.service;

import com.zerobase.zerostore.domain.User;
import com.zerobase.zerostore.dto.UserRequestDto;
import com.zerobase.zerostore.exception.CustomException;
import com.zerobase.zerostore.repository.UserRepository;
import com.zerobase.zerostore.type.ErrorCode;
import com.zerobase.zerostore.type.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registerUser(UserRequestDto userRequestDto) {
        if (userRepository.existsByPhoneNumber(userRequestDto.getPhoneNumber())) {
            throw new CustomException(ErrorCode.USER_ALREADY_REGISTERED);
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
                .orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));
        user.changeRole();
        userRepository.save(user);
    }
}
