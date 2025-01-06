package com.zerobase.zerostore.controller;

import com.zerobase.zerostore.dto.CommonResponseUtil;
import com.zerobase.zerostore.dto.LoginRequestDto;
import com.zerobase.zerostore.dto.UserRequestDto;
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

    @Operation(summary = "회원가입", description = "사용자가 회원가입을 합니다. 사용자 정보를 입력하고 가입을 요청합니다.")
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @RequestBody @Valid UserRequestDto userRequestDto) {
        userService.registerUser(userRequestDto);
        return ResponseEntity.ok(CommonResponseUtil.success("회원가입 성공"));
    }

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

    @Operation(summary = "로그인", description = "사용자가 로그인 요청을 합니다. 로그인 정보가 유효하면 JWT 토큰을 반환합니다.")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto) {
        return ResponseEntity.ok(CommonResponseUtil.success("로그인 성공", userService.login(loginRequestDto)));
    }
}
