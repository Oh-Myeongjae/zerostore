package com.zerobase.zerostore.controller;

import com.zerobase.zerostore.dto.CommonResponseUtil;
import com.zerobase.zerostore.dto.LoginRequestDto;
import com.zerobase.zerostore.dto.UserRequestDto;
import com.zerobase.zerostore.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid UserRequestDto userRequestDto) {
        userService.registerUser(userRequestDto);
        return ResponseEntity.ok(CommonResponseUtil.success("회원가입 성공"));
    }

    @PostMapping("/{id}/partner")
    public ResponseEntity<?> applyForPartner(@PathVariable Long id) {
        userService.applyForPartner(id);
        return ResponseEntity.ok(CommonResponseUtil.success("파트너 전환 성공"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto) {
        return ResponseEntity.ok(CommonResponseUtil.success("로그인 성공", userService.login(loginRequestDto)));
    }

}

