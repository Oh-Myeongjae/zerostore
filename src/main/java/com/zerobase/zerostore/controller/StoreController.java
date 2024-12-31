package com.zerobase.zerostore.controller;

import com.zerobase.zerostore.dto.CommonResponse;
import com.zerobase.zerostore.dto.StoreRequestDto;
import com.zerobase.zerostore.security.UserDetailsImpl;
import com.zerobase.zerostore.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/store")
public class StoreController {

    private final StoreService storeService;

    @PostMapping("/register")
    public ResponseEntity<?> registerStore(
            @RequestBody StoreRequestDto storeRequest,
            @AuthenticationPrincipal UserDetailsImpl user// 인증된 사용자 정보
    ) {

        if(user==null){
            return ResponseEntity.ok(CommonResponse.error(403,"상점등록을 위한 권한이 없습니다."));
        }

        storeService.registerStore(user.getUseNumber(),storeRequest);

        return ResponseEntity.ok(CommonResponse.success("상점등록 성공"));
    }
}

