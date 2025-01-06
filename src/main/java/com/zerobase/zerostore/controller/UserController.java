package com.zerobase.zerostore.controller;

import com.zerobase.zerostore.dto.CommonResponseUtil;
import com.zerobase.zerostore.dto.LoginRequest;
import com.zerobase.zerostore.dto.UserRequest;
import com.zerobase.zerostore.security.UserDetailsImpl;
import com.zerobase.zerostore.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "회원 관리 API")
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    /**
     * 회원가입을 처리하는 메서드입니다.
     * 사용자가 제공한 정보를 기반으로 새 계정을 생성합니다.
     *
     * @param userRequest 회원가입 요청 정보 (이름, 전화번호, 비밀번호 등)
     * @return 성공 메시지를 포함한 응답
     */
    @Operation(summary = "회원가입", description = "사용자가 회원가입을 합니다. 사용자 정보를 입력하고 가입을 요청합니다.")
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @RequestBody @Valid UserRequest userRequest) {
        userService.registerUser(userRequest);
        return ResponseEntity.ok(CommonResponseUtil.success("회원가입 성공"));
    }

    /**
     * 사용자가 파트너로 전환을 신청하는 메서드입니다.
     * 인증된 사용자의 정보가 필요하며, 인증되지 않은 사용자는 403 응답을 받습니다.
     *
     * @param user 인증된 사용자 정보
     * @return 파트너 전환 성공 메시지를 포함한 응답
     */
    @Operation(summary = "파트너 전환", description = "로그인된 사용자가 파트너로 전환 신청을 합니다. 로그인되지 않은 경우 403 에러가 발생합니다.")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/partner")
    public ResponseEntity<?> applyForPartner(
            @AuthenticationPrincipal UserDetailsImpl user) {
        // 로그인 확인
        if (user == null) {
            return ResponseEntity.ok(CommonResponseUtil.error(403, "로그인이 필요한 서비스입니다."));
        }

        // 파트너 전환 로직
        userService.applyForPartner(user.getUser().getId());
        return ResponseEntity.ok(CommonResponseUtil.success("파트너 전환 성공"));
    }

    /**
     * 사용자가 로그인 요청을 수행하는 메서드입니다.
     * 유효한 로그인 정보를 제공하면 JWT 토큰을 반환합니다.
     *
     * @param loginRequest 로그인 요청 정보 (전화번호, 비밀번호)
     * @return JWT 토큰을 포함한 로그인 성공 메시지
     */
    @Operation(summary = "로그인", description = "사용자가 로그인 요청을 합니다. 로그인 정보가 유효하면 JWT 토큰을 반환합니다.")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(CommonResponseUtil.success("로그인 성공", userService.login(loginRequest)));
    }
}
