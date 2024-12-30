package com.zerobase.zerostore.controller;

import com.zerobase.zerostore.dto.CommonResponse;
import com.zerobase.zerostore.dto.LoginRequestDto;
import com.zerobase.zerostore.dto.UserRequestDto;
import com.zerobase.zerostore.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid UserRequestDto userRequestDto) {
        userService.registerUser(userRequestDto);
        CommonResponse<Object> response = CommonResponse.builder()
                .message("회원가입 성공")
                .status(200)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/partner")
    public ResponseEntity<?> applyForPartner(@PathVariable Long id) {
        userService.applyForPartner(id);

        CommonResponse<Object> response = CommonResponse.builder()
                .message("파트너 전환 성공")
                .status(200)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto) {
        CommonResponse<Object> response = CommonResponse.builder()
                .message("로그인 성공")
                .status(200)
                .data(userService.login(loginRequestDto))
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/test")
    public ResponseEntity<?> test(@AuthenticationPrincipal UserDetails userDetails){
        System.out.println("userDetails.getUsername() = " + userDetails.getUsername());
        return ResponseEntity.ok("성공");            
    }
}

