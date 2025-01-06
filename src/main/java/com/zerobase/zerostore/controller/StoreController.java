package com.zerobase.zerostore.controller;

import com.zerobase.zerostore.dto.CommonResponseUtil;
import com.zerobase.zerostore.dto.StoreRequest;
import com.zerobase.zerostore.dto.StoreResponse;
import com.zerobase.zerostore.dto.StoreUpdateRequest;
import com.zerobase.zerostore.security.UserDetailsImpl;
import com.zerobase.zerostore.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Store", description = "상점 관리 API")
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/store")
public class StoreController {

    private final StoreService storeService;

    @Operation(summary = "상점 등록", description = "파트너 사용자가 새로운 상점을 등록합니다.")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/register")
    public ResponseEntity<?> registerStore(
            @RequestBody @Valid StoreRequest storeRequest,
            @AuthenticationPrincipal UserDetailsImpl user
    ) {
        if (user == null) {
            return ResponseEntity.ok(CommonResponseUtil.error(403, "상점등록을 위한 권한이 없습니다."));
        }
        storeService.registerStore(user.getUseNumber(), storeRequest);
        return ResponseEntity.ok(CommonResponseUtil.success("상점등록 성공"));
    }

    @Operation(summary = "상점 정보 수정", description = "파트너 사용자가 특정 상점 정보를 수정합니다.")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{storeId}")
    public ResponseEntity<CommonResponseUtil<?>> updateStore(
            @PathVariable Long storeId,
            @AuthenticationPrincipal UserDetailsImpl user,
            @RequestBody @Valid StoreUpdateRequest request
    ) {
        if (user == null) {
            return ResponseEntity.ok(CommonResponseUtil.error(403, "상점정보 수정을 위한 권한이 없습니다."));
        }
        storeService.updateStore(storeId, user.getUser(), request);
        return ResponseEntity.ok(CommonResponseUtil.success("상점 수정 성공"));
    }

    @Operation(summary = "상점 삭제", description = "파트너 사용자가 특정 상점을 삭제합니다.")
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/{storeId}")
    public ResponseEntity<CommonResponseUtil<?>> deleteStore(
            @PathVariable Long storeId,
            @AuthenticationPrincipal UserDetailsImpl user
    ) {
        if (user == null) {
            return ResponseEntity.ok(CommonResponseUtil.error(403, "상점을 삭제하기 위한 권한이 없습니다."));
        }
        storeService.deleteStore(storeId, user.getUser());
        return ResponseEntity.ok(CommonResponseUtil.success("상점 삭제 성공"));
    }

    @Operation(summary = "전체 상점 조회", description = "등록된 모든 상점 정보를 조회합니다.")
    @GetMapping
    public ResponseEntity<CommonResponseUtil<?>> getAllStores() {
        List<StoreResponse> stores = storeService.getAllStores();
        return ResponseEntity.ok(CommonResponseUtil.success("전체 상점 조회 성공", stores));
    }

    @Operation(summary = "특정 상점 조회", description = "상점 ID를 기준으로 특정 상점 정보를 조회합니다.")
    @GetMapping("/{storeId}")
    public ResponseEntity<CommonResponseUtil<?>> getStoreById(@PathVariable Long storeId) {
        StoreResponse store = storeService.getStoreById(storeId);
        return ResponseEntity.ok(CommonResponseUtil.success("상점 조회 성공", store));
    }

    @Operation(summary = "파트너 상점 조회", description = "로그인된 파트너 사용자가 소유한 모든 상점을 조회합니다.")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/owned")
    public ResponseEntity<CommonResponseUtil<?>> getStoresByOwner(@AuthenticationPrincipal UserDetailsImpl user) {
        List<StoreResponse> stores = storeService.getStoresByOwner(user.getUser());
        return ResponseEntity.ok(CommonResponseUtil.success("파트너 상점 조회 성공", stores));
    }
}

