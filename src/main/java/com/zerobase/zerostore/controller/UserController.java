package com.zerobase.zerostore.controller;

import com.zerobase.zerostore.dto.UserRequestDto;
import com.zerobase.zerostore.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody @Valid UserRequestDto userRequestDto) {
        userService.registerUser(userRequestDto);
        return ResponseEntity.ok("회원가입 성공");
    }

    @PostMapping("/{id}/apply-partner")
    public ResponseEntity<String> applyForPartner(@PathVariable Long id) {
        userService.applyForPartner(id);
        return ResponseEntity.ok("파트너 신청 성공");
    }
}

