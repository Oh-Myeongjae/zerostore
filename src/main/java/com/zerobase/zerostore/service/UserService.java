package com.zerobase.zerostore.service;

import com.zerobase.zerostore.domain.User;
import com.zerobase.zerostore.dto.LoginRequest;
import com.zerobase.zerostore.dto.TokenResponse;
import com.zerobase.zerostore.dto.UserRequest;
import com.zerobase.zerostore.exception.CustomException;
import com.zerobase.zerostore.repository.UserRepository;
import com.zerobase.zerostore.security.JwtTokenProvider;
import com.zerobase.zerostore.type.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.zerobase.zerostore.type.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 사용자가 회원가입을 수행하는 메서드입니다.
     * 사용자가 입력한 전화번호가 이미 등록되어 있는지 확인하고,
     * 전화번호가 중복되지 않으면 새 사용자로 등록합니다.
     *
     * @param userRequest 사용자 등록 요청 정보
     * @throws CustomException 이미 등록된 전화번호에 대해 예외를 발생시킴
     */
    @Transactional
    public void registerUser(UserRequest userRequest) {
        // 전화번호 중복 체크
        if (userRepository.existsByPhoneNumber(userRequest.getPhoneNumber())) {
            throw new CustomException(USER_ALREADY_REGISTERED);
        }

        // 사용자 객체 생성 및 저장
        User user = User.builder()
                .name(userRequest.getName())
                .phoneNumber(userRequest.getPhoneNumber())
                .password(passwordEncoder.encode(userRequest.getPassword())) // 비밀번호 암호화
                .role(Role.USER) // 기본 역할 설정
                .build();
        userRepository.save(user);
    }

    /**
     * 사용자가 파트너로 전환 신청을 하는 메서드입니다.
     * 파트너 전환을 위해 사용자의 ID로 사용자 정보를 조회하고,
     * 사용자 역할을 변경한 후 저장합니다.
     *
     * @param userId 사용자 ID
     * @throws CustomException 사용자 정보가 존재하지 않는 경우 예외를 발생시킴
     */
    @Transactional
    public void applyForPartner(Long userId) {
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        // 역할 변경
        user.changeRole(); // 역할을 파트너로 변경
        userRepository.save(user); // 변경된 사용자 정보 저장
    }

    /**
     * 사용자가 로그인하는 메서드입니다.
     * 전화번호와 비밀번호를 통해 사용자를 인증하고,
     * 유효한 사용자라면 JWT 토큰을 생성하여 반환합니다.
     *
     * @param loginRequest 로그인 요청 정보 (전화번호, 비밀번호)
     * @return TokenResponse JWT 토큰을 포함한 응답
     * @throws CustomException 사용자 정보가 없거나, 비밀번호가 일치하지 않으면 예외를 발생시킴
     */
    public TokenResponse login(LoginRequest loginRequest) {
        // 전화번호로 사용자 조회
        User user = userRepository.findByPhoneNumber(loginRequest.getPhoneNumber())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        // 비밀번호 확인
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new CustomException(INVALID_PASSWORD);
        }

        // JWT 토큰 생성 및 반환
        return TokenResponse.builder()
                .token(jwtTokenProvider.generateToken(user.getPhoneNumber()))
                .build();
    }
}

