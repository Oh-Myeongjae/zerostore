package com.zerobase.zerostore.service;

import com.zerobase.zerostore.domain.User;
import com.zerobase.zerostore.dto.UserRequestDto;
import com.zerobase.zerostore.repository.UserRepository;
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
    public User registerUser(UserRequestDto userRequestDto) {
        if (userRepository.existsByPhoneNumber(userRequestDto.getPhoneNumber())) {
            throw new IllegalArgumentException("이미 등록된 전화번호입니다.");
        }

        User user = User.builder()
                .name(userRequestDto.getName())
                .phoneNumber(userRequestDto.getPhoneNumber())
                .password(passwordEncoder.encode(userRequestDto.getPassword()))// 비밀번호 암호화
                .role(Role.USER)// 기본 역할 설정
                .build();
        return userRepository.save(user);
    }

    @Transactional
    public User applyForPartner(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        user.changeRole();
        return userRepository.save(user);
    }
}
