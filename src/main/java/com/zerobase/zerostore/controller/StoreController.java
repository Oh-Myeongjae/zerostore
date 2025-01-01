package com.zerobase.zerostore.controller;

import com.zerobase.zerostore.dto.CommonResponse;
import com.zerobase.zerostore.dto.StoreRequestDto;
import com.zerobase.zerostore.dto.StoreResponseDto;
import com.zerobase.zerostore.dto.StoreUpdateRequestDto;
import com.zerobase.zerostore.security.UserDetailsImpl;
import com.zerobase.zerostore.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

        if (user == null) {
            return ResponseEntity.ok(CommonResponse.error(403, "상점등록을 위한 권한이 없습니다."));
        }

        storeService.registerStore(user.getUseNumber(), storeRequest);

        return ResponseEntity.ok(CommonResponse.success("상점등록 성공"));
    }

    @PutMapping("/{storeId}")
    public ResponseEntity<CommonResponse<?>> updateStore(
            @PathVariable Long storeId,
            @AuthenticationPrincipal UserDetailsImpl user,
            @Validated @RequestBody StoreUpdateRequestDto request
    ) {

        if (user == null) {
            return ResponseEntity.ok(CommonResponse.error(403, "상점정보 수정을 위한 권한이 없습니다."));
        }

        storeService.updateStore(storeId, user.getUser(), request);

        return ResponseEntity.ok(
                CommonResponse.success("상점 수정 성공")
        );
    }

    @DeleteMapping("/{storeId}")
    public ResponseEntity<CommonResponse<?>> deleteStore(
            @PathVariable Long storeId,
            @AuthenticationPrincipal UserDetailsImpl user
    ) {

        if (user == null) {
            return ResponseEntity.ok(CommonResponse.error(403, "상점을 삭제하기 위한 권한이 없습니다."));
        }

        storeService.deleteStore(storeId, user.getUser());

        return ResponseEntity.ok(
                CommonResponse.success("상점 삭제 성공")
        );
    }

    //전체 상점 리스트 조회
    @GetMapping
    public ResponseEntity<CommonResponse<?>> getAllStores() {
        List<StoreResponseDto> stores = storeService.getAllStores();
        return ResponseEntity.ok(CommonResponse.success("전체 상점 조회 성공", stores));
    }

    //특정 상점 정보 조회
    @GetMapping("/{storeId}")
    public ResponseEntity<CommonResponse<?>> getStoreById(@PathVariable Long storeId) {
        StoreResponseDto store = storeService.getStoreById(storeId);
        return ResponseEntity.ok(CommonResponse.success("상점 조회 성공", store));
    }

    //   파트너의 모든 상점 조회
    @GetMapping("/owned")
    public ResponseEntity<CommonResponse<?>> getStoresByOwner(@AuthenticationPrincipal UserDetailsImpl user) {
        List<StoreResponseDto> stores = storeService.getStoresByOwner(user.getUser());
        return ResponseEntity.ok(CommonResponse.success("파트너 상점 조회 성공", stores));
    }
}

